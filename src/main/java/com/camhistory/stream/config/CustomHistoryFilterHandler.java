package com.camhistory.stream.config;

import com.camhistory.stream.entity.HistoryDocument;
import com.camhistory.stream.handlers.MongoDBHistoryEntity;
import org.camunda.bpm.engine.impl.history.event.*;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomHistoryFilterHandler extends DbHistoryEventHandler {

    @Override
    public void handleEvent(HistoryEvent historyEvent)
    {
        if (historyEvent instanceof HistoricProcessInstanceEventEntity
                || historyEvent instanceof HistoricVariableUpdateEventEntity
                || historyEvent instanceof HistoricTaskInstanceEventEntity
                || historyEvent instanceof HistoricActivityInstanceEventEntity)
        {
            MongoDBHistoryEntity dbHistoryEntity = MongoDBHistoryEntity.createDocumentFromHistoryEvent(historyEvent);
            HistoryDocument historyDocument = new HistoryDocument(dbHistoryEntity);
            return;
        }
        super.handleEvent(historyEvent);
    }
}
