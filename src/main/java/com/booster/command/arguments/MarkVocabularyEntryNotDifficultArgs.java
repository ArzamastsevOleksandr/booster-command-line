package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class MarkVocabularyEntryNotDifficultArgs implements Args {

    long id;

}
