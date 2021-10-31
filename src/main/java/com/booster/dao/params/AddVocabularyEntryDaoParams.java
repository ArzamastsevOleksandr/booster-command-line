package com.booster.dao.params;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddVocabularyEntryDaoParams {

    long wordId;
    long languageId;
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
