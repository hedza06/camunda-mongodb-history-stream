package com.camhistory.stream.entity;

import com.camhistory.stream.handlers.MongoDBHistoryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Document(collection = "activities")
public class HistoryDocument {

    @Id
    private String id;

    @NotNull
    private MongoDBHistoryEntity historyEntity;

    public HistoryDocument() { }

    public HistoryDocument(MongoDBHistoryEntity historyEntity) {
        this.historyEntity = historyEntity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MongoDBHistoryEntity getHistoryEntity() {
        return historyEntity;
    }

    public void setHistoryEntity(MongoDBHistoryEntity historyEntity) {
        this.historyEntity = historyEntity;
    }
}
