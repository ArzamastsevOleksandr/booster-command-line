package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

public abstract class ListVocabulariesArgs implements Args {

    public abstract Optional<Long> getId();

    @ToString
    @RequiredArgsConstructor
    private static class NonEmptyArgs extends ListVocabulariesArgs {

        private final long vocabularyId;

        @Override
        public Optional<Long> getId() {
            return Optional.of(vocabularyId);
        }

    }

    @ToString
    private static class EmptyArgs extends ListVocabulariesArgs {
        @Override
        public Optional<Long> getId() {
            return Optional.empty();
        }
    }

    public static ListVocabulariesArgs of(long id) {
        return new NonEmptyArgs(id);
    }

    public static ListVocabulariesArgs empty() {
        return new EmptyArgs();
    }

}
