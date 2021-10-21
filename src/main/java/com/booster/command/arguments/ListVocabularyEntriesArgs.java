package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

public abstract class ListVocabularyEntriesArgs implements Args {

    public abstract Optional<Long> getId();

    @ToString
    @RequiredArgsConstructor
    private static class NonEmptyArgs extends ListVocabularyEntriesArgs {

        private final long vocabularyEntryId;

        @Override
        public Optional<Long> getId() {
            return Optional.of(vocabularyEntryId);
        }

    }

    @ToString
    private static class EmptyArgs extends ListVocabularyEntriesArgs {
        @Override
        public Optional<Long> getId() {
            return Optional.empty();
        }
    }

    public static ListVocabularyEntriesArgs of(long id) {
        return new NonEmptyArgs(id);
    }

    public static ListVocabularyEntriesArgs empty() {
        return new EmptyArgs();
    }

}
