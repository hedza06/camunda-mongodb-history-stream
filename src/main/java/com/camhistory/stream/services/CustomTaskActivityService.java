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
     * Processing process instance events
     *
     * @param historyEvent history event
     */
    public void processProcessInstanceEvent(HistoryEvent historyEvent) {
        // no implementation...
    }

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
    @SuppressWarnings("unchecked")
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
            taskEventEntity.setCandidateUsers(taskEventEntity.getCandidateUsers()); // TODO: set candidate users
            taskEventEntity.setStartTime(historicTaskInstance.getStartTime());

            Map<String, Long> vars = (Map<String, Long>) variableMapping.get(historicTaskInstance.getExecutionId());
            taskEventEntity.setCustomerId(vars.get("customerId"));
            taskEventEntity.setProductId(vars.get("productId"));
          
            // TODO: check vars for null
            // 1. fetch customerId and productId if vars is null
            // 2. fetch customerId and productId from map if vars is not null

            // 3. TODO: fetch product and customer general data...

            variableMapping.remove(historicTaskInstance.getExecutionId());

            customTaskActivityRepository.save(taskEventEntity);
        }
        else if (eventType.equals("complete"))
        {
            // 1. fetch by executionId and taskId
            TaskEventEntity taskEventEntity = customTaskActivityRepository.findByExecutionId(
                historicTaskInstance.getExecutionId()
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
}
