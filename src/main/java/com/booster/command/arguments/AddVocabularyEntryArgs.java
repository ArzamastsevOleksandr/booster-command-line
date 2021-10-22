package com.booster.command.arguments;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class AddVocabularyEntryArgs implements Args {

    Long wordId;
    Long vocabularyId;
    String definition;
    List<Long> synonymIds;
    List<Long> antonymIds;

}
