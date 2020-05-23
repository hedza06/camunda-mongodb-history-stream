package com.camhistory.stream.handlers;

import com.camhistory.stream.entity.HistoryDocument;
import com.camhistory.stream.repository.HistoryDocumentRepository;
import org.camunda.bpm.engine.impl.history.event.*;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CustomHistoryEventHandler implements HistoryEventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHistoryEventHandler.class);
    private static final CustomHistoryEventHandler INSTANCE = new CustomHistoryEventHandler();

    @Autowired
    private HistoryDocumentRepository historyDocumentRepository;


    public static CustomHistoryEventHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void handleEvent(HistoryEvent historyEvent)
    {
        LOGGER.info("Handling custom event...");

        if (historyEvent instanceof HistoricProcessInstanceEventEntity
                || historyEvent instanceof HistoricVariableUpdateEventEntity
                || historyEvent instanceof HistoricTaskInstanceEventEntity
                || historyEvent instanceof HistoricActivityInstanceEventEntity)
        {
            MongoDBHistoryEntity dbHistoryEntity = MongoDBHistoryEntity.createDocumentFromHistoryEvent(historyEvent);
            HistoryDocument historyDocument = new HistoryDocument(dbHistoryEntity);
            historyDocumentRepository.save(historyDocument);
        }
    }

    @Override
    public void handleEvents(List<HistoryEvent> historyEvents)
    {
        for (HistoryEvent historyEvent : historyEvents) {
            handleEvent(historyEvent);
        }
    }
}
