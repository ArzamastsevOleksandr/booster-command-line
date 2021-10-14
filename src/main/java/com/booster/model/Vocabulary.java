package com.booster.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class Vocabulary {

    private long id;
    private String name;
    private Timestamp createdAt;

    private String languageName;
    private long languageBeingLearnedId;

}
