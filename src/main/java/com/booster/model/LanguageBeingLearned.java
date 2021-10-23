package com.booster.model;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Value
@Builder
public class LanguageBeingLearned {

    long id;
    Timestamp createdAt;

    Language language;

}
