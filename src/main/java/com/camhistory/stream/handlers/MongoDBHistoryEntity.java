package com.camhistory.stream.handlers;

import org.camunda.bpm.engine.impl.history.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Description;

import java.util.ArrayList;
import java.util.List;

@Description("")
public class MongoDBHistoryEntity extends HistoricProcessInstanceEventEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBHistoryEntity.class);

    private List<HistoricTaskInstanceEventEntity> tasks          = null;
    private List<HistoricVariableUpdateEventEntity> variables    = null;
    private List<HistoricActivityInstanceEventEntity> activities = null;

    public MongoDBHistoryEntity() {
        // not used...
    }

    /**
     * Factory method for creating document from history event
     *
     * @param historyEvent given history event
     * @return MongoDBHistoryEntity Document
     */
    public static MongoDBHistoryEntity createDocumentFromHistoryEvent(HistoryEvent historyEvent)
    {
        MongoDBHistoryEntity dbHistoryEntity = new MongoDBHistoryEntity();
        dbHistoryEntity.setId(historyEvent.getId());
        dbHistoryEntity.setProcessInstanceId(historyEvent.getProcessInstanceId());

        if (historyEvent instanceof HistoricProcessInstanceEventEntity)
        {
            HistoricProcessInstanceEventEntity historicProcessInstance
                    = (HistoricProcessInstanceEventEntity) historyEvent;
            handleProcessInstanceEventEntity(dbHistoryEntity, historicProcessInstance);
        }
        else if (historyEvent instanceof HistoricActivityInstanceEventEntity)
        {
            HistoricActivityInstanceEventEntity historicActivityInstance
                    = (HistoricActivityInstanceEventEntity) historyEvent;
            handleActivityInstanceEventEntity(dbHistoryEntity, historicActivityInstance);
        }
        else if (historyEvent instanceof HistoricTaskInstanceEventEntity)
        {
            HistoricTaskInstanceEventEntity historicTaskInstance = (HistoricTaskInstanceEventEntity) historyEvent;
            handleTaskInstanceEventEntity(dbHistoryEntity, historicTaskInstance);
        }
        else if (historyEvent instanceof HistoricVariableUpdateEventEntity)
        {
            HistoricVariableUpdateEventEntity historicVariableUpdate = (HistoricVariableUpdateEventEntity) historyEvent;
            handleVariableUpdateEventEntity(dbHistoryEntity, historicVariableUpdate);
        }
        else {
            LOGGER.warn("No event detected!");
        }
        return dbHistoryEntity;
    }

    /**
     * Building historic process instance data
     *
     * @param dbHistoryEntity document
     * @param historicProcessInstance historic process instance
     */
    private static void handleProcessInstanceEventEntity(MongoDBHistoryEntity dbHistoryEntity,
                                                         HistoricProcessInstanceEventEntity historicProcessInstance)
    {
        dbHistoryEntity.setExecutionId(historicProcessInstance.getExecutionId());
        dbHistoryEntity.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());

        dbHistoryEntity.setStartActivityId(historicProcessInstance.getStartActivityId());
        dbHistoryEntity.setEndActivityId(historicProcessInstance.getEndActivityId());
        dbHistoryEntity.setStartTime(historicProcessInstance.getStartTime());
        dbHistoryEntity.setEndTime(historicProcessInstance.getEndTime());
        dbHistoryEntity.setDurationInMillis(historicProcessInstance.getDurationInMillis());

        dbHistoryEntity.setBusinessKey(historicProcessInstance.getBusinessKey());
        dbHistoryEntity.setStartUserId(historicProcessInstance.getStartUserId());
        dbHistoryEntity.setDeleteReason(historicProcessInstance.getDeleteReason());
        dbHistoryEntity.setSuperProcessInstanceId(historicProcessInstance.getSuperProcessInstanceId());
    }

    /**
     * Building activity instance data
     *
     * @param dbHistoryEntity document
     * @param historicActivityInstance historic activity instance
     */
    private static void handleActivityInstanceEventEntity(MongoDBHistoryEntity dbHistoryEntity,
                                                          HistoricActivityInstanceEventEntity historicActivityInstance)
    {
        dbHistoryEntity.addHistoricActivityInstanceEvent(historicActivityInstance);
    }

    /**
     * Building task instance data
     *
     * @param dbHistoryEntity document
     * @param historicTaskInstance historic task instance
     */
    private static void handleTaskInstanceEventEntity(MongoDBHistoryEntity dbHistoryEntity,
                                                      HistoricTaskInstanceEventEntity historicTaskInstance)
    {
        dbHistoryEntity.addHistoricTaskInstanceEvent(historicTaskInstance);
    }

    /**
     * Building variable update data
     *
     * @param dbHistoryEntity document
     * @param historicVariableUpdate historic variable update
     */
    private static void handleVariableUpdateEventEntity(MongoDBHistoryEntity dbHistoryEntity,
                                                        HistoricVariableUpdateEventEntity historicVariableUpdate)
    {
        dbHistoryEntity.addHistoricVariableUpdateEvent(historicVariableUpdate);
    }

    /**
     * Adding historic activity instance to activities list
     *
     * @param activityInstanceEvent activity instance
     */
    private void addHistoricActivityInstanceEvent(HistoricActivityInstanceEventEntity activityInstanceEvent)
    {
        if (activities == null) {
            activities = new ArrayList<>();
        }
        activities.add(activityInstanceEvent);
    }

    /**
     * Adding task instance to task list
     *
     * @param taskInstanceEvent task instance
     */
    private void addHistoricTaskInstanceEvent(HistoricTaskInstanceEventEntity taskInstanceEvent)
    {
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
        tasks.add(taskInstanceEvent);
    }

    /**
     * Adding variable update to variables list
     *
     * @param variableUpdateEvent variable update
     */
    private void addHistoricVariableUpdateEvent(HistoricVariableUpdateEventEntity variableUpdateEvent)
    {
        if (variables == null) {
            variables = new ArrayList<>();
        }
        variables.add(variableUpdateEvent);
    }


    public List<HistoricVariableUpdateEventEntity> getVariables() {
        return variables;
    }

    public void setVariables(List<HistoricVariableUpdateEventEntity> variables) {
        this.variables = variables;
    }

    public List<HistoricActivityInstanceEventEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<HistoricActivityInstanceEventEntity> activities) {
        this.activities = activities;
    }

    public List<HistoricTaskInstanceEventEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<HistoricTaskInstanceEventEntity> tasks) {
        this.tasks = tasks;
    }
}
