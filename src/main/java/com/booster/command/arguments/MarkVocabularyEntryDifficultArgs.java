package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class MarkVocabularyEntryDifficultArgs implements Args {

    long id;

}
