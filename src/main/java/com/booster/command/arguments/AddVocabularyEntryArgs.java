package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class AddVocabularyEntryArgs implements Args {

    Long wordId;
    Long vocabularyId;
    List<Long> synonymIds;
    List<Long> antonymIds;

}
