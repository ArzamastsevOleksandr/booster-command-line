package com.booster.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class LanguageBeingLearned {

    private long id;
    private Timestamp createdAt;

    private Language language;

}
