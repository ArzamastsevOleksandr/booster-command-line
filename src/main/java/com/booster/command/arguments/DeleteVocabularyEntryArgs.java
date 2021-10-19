package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class DeleteVocabularyEntryArgs implements Args {

    long id;

}
