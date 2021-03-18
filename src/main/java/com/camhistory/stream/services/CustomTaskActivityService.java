package com.camhistory.stream.services;

import com.camhistory.stream.CustomTaskActivityRepository;
import com.camhistory.stream.entity.TaskEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricTaskInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoricVariableUpdateEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.variable.Variables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomTaskActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomTaskActivityService.class);
    private final Map<String, Object> variableMapping = new HashMap<>();

    @Autowired
    private CustomTaskActivityRepository customTaskActivityRepository;

    /**
     * Processing variable update historic event
     *
     * @param historyEvent history event
     */
    public void processVariableUpdateEvent(HistoryEvent historyEvent)
    {
        if (historyEvent instanceof HistoricVariableUpdateEventEntity)
        {
            HistoricVariableUpdateEventEntity variableEventEntity = (HistoricVariableUpdateEventEntity) historyEvent;
            String variableName = variableEventEntity.getVariableName();
            if (variableName.equals("customerId") || variableName.equals("productId"))
            {
                appendVariableIdToVariableMap(
                    variableName, variableEventEntity.getLongValue(), variableEventEntity.getExecutionId()
                );
            }
        }
    }

    /**
     * Process historic task instance event
     *
     * @param historyEvent history event
     */
    public void processTaskInstanceEvent(HistoryEvent historyEvent)
    {
        if (historyEvent instanceof HistoricTaskInstanceEventEntity)
        {
            LOGGER.info("History task instance invoked. Event type: {}", historyEvent.getEventType());
            HistoricTaskInstanceEventEntity historicTaskInstance = (HistoricTaskInstanceEventEntity) historyEvent;
            handleTaskInstanceEventBasedOnEventType(historicTaskInstance, historyEvent.getEventType());
        }
    }

    /**
     * Append variable id to variable map
     *
     * @param varName variable name
     * @param longValue variable long value
     * @param executionId process execution identifier
     */
    @SuppressWarnings("unchecked")
    private void appendVariableIdToVariableMap(String varName, Long longValue, String executionId)
    {
        if (variableMapping.containsKey(executionId))
        {
            Map<String, Long> vars = (Map<String, Long>) variableMapping.get(executionId);

            Map<String, Long> data = new HashMap<>(vars);
            data.put(varName, longValue);

            variableMapping.put(executionId, data);
        }
        else {
            variableMapping.put(executionId, Variables.createVariables().putValue(varName, longValue));
        }
    }

    /**
     * Handling task instance event based on event type
     *
     * @param historicTaskInstance historic task instance
     * @param eventType historic event type
     */
    private void handleTaskInstanceEventBasedOnEventType(HistoricTaskInstanceEventEntity historicTaskInstance,
                                                         String eventType)
    {
        if (eventType.equals("create"))
        {
            TaskEventEntity taskEventEntity = new TaskEventEntity();
            taskEventEntity.setProcessDefinitionKey(historicTaskInstance.getProcessDefinitionKey());
            taskEventEntity.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
            taskEventEntity.setTaskId(historicTaskInstance.getTaskId());
            taskEventEntity.setTaskName(historicTaskInstance.getName());
            taskEventEntity.setTaskInstanceId(historicTaskInstance.getTaskId());
            taskEventEntity.setLastAssignee(historicTaskInstance.getAssignee());
            taskEventEntity.setStartTime(historicTaskInstance.getStartTime());

            fetchCustomerAndProductRelevantData(taskEventEntity, historicTaskInstance);
            variableMapping.remove(historicTaskInstance.getExecutionId());

            customTaskActivityRepository.save(taskEventEntity);
        }
        else if (eventType.equals("complete"))
        {
            TaskEventEntity taskEventEntity = customTaskActivityRepository.findByExecutionIdAndTaskId(
                historicTaskInstance.getExecutionId(), historicTaskInstance.getTaskId()
            );
            if (taskEventEntity != null)
            {
                taskEventEntity.setEndTime(
                    historicTaskInstance.getEndTime() != null ? historicTaskInstance.getEndTime() : new Date()
                );
                customTaskActivityRepository.save(taskEventEntity);
            }
        }
    }

    /**
     * Fetch customer and product relevant data
     *
     * @param taskEventEntity task event entity reference
     * @param historicTaskInstance historic task instance
     */
    @SuppressWarnings("unchecked")
    private void fetchCustomerAndProductRelevantData(TaskEventEntity taskEventEntity,
                                                     HistoricTaskInstanceEventEntity historicTaskInstance)
    {
        String executionId = historicTaskInstance.getExecutionId();
        Map<String, Long> vars = (Map<String, Long>) variableMapping.get(executionId);
        if (vars != null && !vars.isEmpty())
        {
            taskEventEntity.setCustomerId(vars.get("customerId"));
            taskEventEntity.setProductId(vars.get("productId"));
        }
        else
        {
            TaskEventEntity storedTaskEntity = customTaskActivityRepository.findByExecutionIdAndTaskId(
                executionId, historicTaskInstance.getTaskId()
            );
            taskEventEntity.setCustomerId(storedTaskEntity.getCustomerId());
            taskEventEntity.setProductId(storedTaskEntity.getProductId());
        }
    }
}
