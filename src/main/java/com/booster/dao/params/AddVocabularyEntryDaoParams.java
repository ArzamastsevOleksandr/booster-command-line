package com.booster.dao.params;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.List;

@Value
@Builder
public class AddVocabularyEntryDaoParams {

    long wordId;
    long vocabularyId;
    String definition;

    @Builder.Default
    Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    @Builder.Default
    int correctAnswersCount = 0;

    @Builder.Default
    List<Long> synonymIds = List.of();
    @Builder.Default
    List<Long> antonymIds = List.of();

}
