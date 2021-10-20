package com.booster.command.arguments;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

public abstract class ListLanguagesBeingLearnedArgs implements Args {

    public abstract Optional<Long> getId();

    @ToString
    @RequiredArgsConstructor
    private static class NonEmptyArgs extends ListLanguagesBeingLearnedArgs {

        private final long languageId;

        @Override
        public Optional<Long> getId() {
            return Optional.of(languageId);
        }

    }

    @ToString
    private static class EmptyArgs extends ListLanguagesBeingLearnedArgs {
        @Override
        public Optional<Long> getId() {
            return Optional.empty();
        }
    }

    public static ListLanguagesBeingLearnedArgs of(long id) {
        return new NonEmptyArgs(id);
    }

    public static ListLanguagesBeingLearnedArgs empty() {
        return new EmptyArgs();
    }

}
