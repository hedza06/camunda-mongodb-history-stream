package com.camhistory.stream.repository;

import com.camhistory.stream.entity.HistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoryDocumentRepository extends MongoRepository<HistoryDocument, String> { }
