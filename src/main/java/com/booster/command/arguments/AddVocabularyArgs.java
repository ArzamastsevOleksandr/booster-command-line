package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class AddVocabularyArgs implements Args {

    long languageBeingLearnedId;
    String name;

}
