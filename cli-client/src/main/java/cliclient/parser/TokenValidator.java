package cliclient.parser;

import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import cliclient.util.CollectionUtils;
import cliclient.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class TokenValidator {

    private final NumberUtil numberUtil;

    TokenValidationResult validate(List<Token> tokens) {
        try {
            if (tokens.isEmpty()) {
                return TokenValidationResult.empty();
            }
            validateFirstToken(tokens);
            if (tokens.size() == 1) {
                return TokenValidationResult.success(tokens);
            }
            if (tokens.size() == 2) {
                checkSequenceIsHelpOnCommand(tokens);
                return TokenValidationResult.success(tokens);
            }
            List<Token> commandArguments = CollectionUtils.sublist(tokens, 1);
            checkCommandArgumentsSize(commandArguments);

            validateCommandArguments(commandArguments);

            return TokenValidationResult.success(tokens);
        } catch (TokenValidationException e) {
            return TokenValidationResult.withErrors(e.errors);
        }
    }

    private void validateCommandArguments(List<Token> commandArguments) {
        for (int i = 0; i < commandArguments.size(); i += 3) {
            Token flag = commandArguments.get(i);
            checkIsFlag(flag);

            Token separator = commandArguments.get(i + 1);
            checkIsSeparator(separator);

            Token value = commandArguments.get(i + 2);
            checkIsNotFlag(value);

            validateFlagValueBasedOnFlagType(flag, value);
        }
    }

    private void validateFirstToken(List<Token> tokens) {
        Token command = eatFrontCommand(tokens);
        checkIsCommand(command);
    }

    private Token eatFrontCommand(List<Token> tokens) {
        return tokens.get(0);
    }

    private void checkIsCommand(Token token) {
        if (Token.isNotCommand(token)) {
            throw new TokenValidationException("Sequence must start with a command");
        }
    }

    private void checkSequenceIsHelpOnCommand(List<Token> tokens) {
        var help = Command.fromString(tokens.get(0).value());
        if (help != Command.HELP && Token.isNotCommand(tokens.get(1))) {
            throw new TokenValidationException("Allowed format for 2 tokens is: " + Command.HELP + " [command]");
        }
    }

    private void checkCommandArgumentsSize(List<Token> tokens) {
        if (tokens.size() % 3 != 0) {
            throw new TokenValidationException("Arguments must follow a pattern of [flag separator value]");
        }
    }

    private void checkIsFlag(Token token) {
        if (Token.isNotFlag(token)) {
            var error = "Expected flag, got: " + token.value() + " with type: " + tokenTypeToLowerCase(token);
            throw new TokenValidationException(error);
        }
    }

    private void checkIsSeparator(Token token) {
        if (Token.isNotSeparator(token)) {
            var error = "Expected separator, got: " + token.value() + " with type: " + tokenTypeToLowerCase(token);
            throw new TokenValidationException(error);
        }
    }

    private void checkIsNotFlag(Token token) {
        if (Token.isFlag(token)) {
            throw new TokenValidationException("Separator is followed by a flag: " + token.value());
        }
    }

    private void validateFlagValueBasedOnFlagType(Token expectedFlag, Token expectedValue) {
        var flagType = FlagType.fromString(expectedFlag.value());
        String flagTypeName = flagType.name();
        String value = expectedValue.value();
        switch (flagType) {
            case ID, NOTE_ID, VOCABULARY_ENTRY_ID -> checkIdIsPositiveLong(flagTypeName, value);
            case CORRECT_ANSWERS_COUNT, ENTRIES_PER_VOCABULARY_TRAINING_SESSION,
                    PAGINATION, NOTES_PAGINATION, LANGUAGES_PAGINATION, TAGS_PAGINATION, VOCABULARY_PAGINATION -> checkValueIsPositiveInteger(flagTypeName, value);
            case MODE_VOCABULARY -> checkVocabularyTrainingSessionModeIsCorrect(value);
        }
    }

    private void checkVocabularyTrainingSessionModeIsCorrect(String value) {
        if (VocabularyTrainingSessionMode.isUnrecognized(value)) {
            throw new TokenValidationException(
                    "Unrecognized training session mode: " + value,
                    "Available modes are: " + VocabularyTrainingSessionMode.modesToString()
            );
        }
    }

    private void checkValueIsPositiveInteger(String name, String value) {
        if (numberUtil.isNotPositiveInteger(value)) {
            throw new TokenValidationException(name + " argument must be a positive integer number, got: " + value);
        }
    }

    private void checkIdIsPositiveLong(String name, String value) {
        if (numberUtil.isNotPositiveLong(value)) {
            throw new TokenValidationException(name + " argument must be a positive long number, got: " + value);
        }
    }

    private String tokenTypeToLowerCase(Token token) {
        return token.type().toString().toLowerCase();
    }

    private static class TokenValidationException extends RuntimeException {
        final List<String> errors;

        TokenValidationException(String... errors) {
            this.errors = List.of(errors);
        }
    }

}
