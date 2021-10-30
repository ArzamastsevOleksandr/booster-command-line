package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@RequiredArgsConstructor
public class ListVocabularyEntriesArgs implements Args {

    Long id;

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public static ListVocabularyEntriesArgs empty() {
        return new ListVocabularyEntriesArgs(null);
    }

}
