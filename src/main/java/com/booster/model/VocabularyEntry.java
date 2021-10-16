package com.booster.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class VocabularyEntry {

    private long id;
    private Timestamp createdAt;
    private int correctAnswersCount;

    // todo: feat 1 ve can belong to N v
    private String vocabularyName;
    private Word word;

}
