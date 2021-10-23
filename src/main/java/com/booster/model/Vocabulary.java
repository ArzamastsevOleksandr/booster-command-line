package com.booster.model;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;

@Value
@Builder
public class Vocabulary {

    long id;
    String name;
    Timestamp createdAt;

    String languageName;
    long languageBeingLearnedId;

    // todo: numberOfEntries
    // todo: % of entries learned
}
