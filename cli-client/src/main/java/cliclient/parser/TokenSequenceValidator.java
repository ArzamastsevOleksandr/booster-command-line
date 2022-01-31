package cliclient.parser;

import cliclient.command.FlagType;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import cliclient.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
class TokenSequenceValidator {

    private final NumberUtil numberUtil;

    // todo: implement support for flags with no values
    TokenValidationResult validate(List<Token> tokens) {
        try {
            checkIfTokensAreEmpty(tokens);

            validateFirstToken(tokens);
            if (tokens.size() == 1) {
                return TokenValidationResult.success(tokens);
            }
            List<Token> commandArguments = new ArrayList<>(tokens.subList(1, tokens.size()));
            checkCommandArgumentsSize(commandArguments);

            validateCommandArguments(commandArguments);

            return TokenValidationResult.success(tokens);
        } catch (TokenValidationException e) {
            return TokenValidationResult.withErrors(e.errors);
        }
    }

    private void validateCommandArguments(List<Token> commandArguments) {
        for (int i = 0; i < commandArguments.size(); i += 3) {
            Token expectedFlag = commandArguments.get(i);
            checkIfTokenIsFlag(expectedFlag);

            Token expectedSeparator = commandArguments.get(i + 1);
            checkIfTokenIsSeparator(expectedSeparator);

            Token expectedValue = commandArguments.get(i + 2);
            checkIfTokenIsNotFlag(expectedValue);

            validateFlagValueBasedOnFlagType(expectedFlag, expectedValue);
        }
    }

    private void validateFirstToken(List<Token> tokens) {
        Token expectedCommand = eatFrontCommand(tokens);
        checkIfTokenIsCommand(expectedCommand);
    }

    private void checkIfTokensAreEmpty(List<Token> tokens) {
        if (tokens.isEmpty())
            throw new TokenValidationException("Token sequence must consist of at least one argument");
    }

    private Token eatFrontCommand(List<Token> tokens) {
        return tokens.get(0);
    }

    private void checkIfTokenIsCommand(Token token) {
        if (Token.isNotCommand(token))
            throw new TokenValidationException("Token sequence must start with a command");
    }

    private void checkCommandArgumentsSize(List<Token> tokens) {
        if (tokens.size() % 3 != 0)
            throw new TokenValidationException("Arguments must follow a pattern of flag -> separator -> value");
    }

    private void checkIfTokenIsFlag(Token token) {
        if (Token.isNotFlag(token))
            throw new TokenValidationException(
                    "Expected flag, got: " + token.getValue() + " with type: " + tokenTypeToLowerCaseString(token)
            );
    }

    private void checkIfTokenIsSeparator(Token token) {
        if (Token.isNotSeparator(token))
            throw new TokenValidationException(
                    "Expected separator, got: " + token.getValue() + " with type: " + tokenTypeToLowerCaseString(token)
            );
    }

    // todo: more clear naming
    private void checkIfTokenIsNotFlag(Token token) {
        if (Token.isFlag(token))
            throw new TokenValidationException("Separator is followed by a flag: " + token.getValue());
    }

    private void validateFlagValueBasedOnFlagType(Token expectedFlag, Token expectedValue) {
        FlagType flagType = FlagType.fromString(expectedFlag.getValue());
        switch (flagType) {
            case ID -> checkIfIdIsPositiveLongNumber("Id", expectedValue.getValue());
            case LANGUAGE_ID -> checkIfIdIsPositiveLongNumber("Language id", expectedValue.getValue());
            case NOTE_ID -> checkIfIdIsPositiveLongNumber("Note id", expectedValue.getValue());
            case VOCABULARY_ENTRY_ID -> checkIfIdIsPositiveLongNumber("Vocabulary entry id", expectedValue.getValue());
            case CORRECT_ANSWERS_COUNT -> checkIfValueIsPositiveIntegerNumber("Correct answers count", expectedValue.getValue());
            case PAGINATION -> checkIfValueIsPositiveIntegerNumber("Pagination", expectedValue.getValue());
            case VOCABULARY_TRAINING_SESSION_SIZE -> checkIfValueIsPositiveIntegerNumber("Vocabulary training session size", expectedValue.getValue());
            case MODE_VOCABULARY -> checkIfVocabularyTrainingSessionModeIsCorrect(expectedValue.getValue());
        }
    }

    private void checkIfVocabularyTrainingSessionModeIsCorrect(String value) {
        if (VocabularyTrainingSessionMode.isUnrecognized(value)) {
            throw new TokenValidationException(
                    "Unrecognized training session mode: " + value,
                    "Available modes are: " + VocabularyTrainingSessionMode.modesToString()
            );
        }
    }

    private void checkIfValueIsPositiveIntegerNumber(String name, String value) {
        if (numberUtil.isNotPositiveInteger(value))
            throw new TokenValidationException(name + " argument must be a positive integer number, got: " + value);
    }

    private void checkIfIdIsPositiveLongNumber(String name, String value) {
        if (numberUtil.isNotPositiveLong(value))
            throw new TokenValidationException(name + " argument must be a positive long number, got: " + value);
    }

    private String tokenTypeToLowerCaseString(Token token) {
        return token.getType().toString().toLowerCase();
    }

    private static class TokenValidationException extends RuntimeException {

        final List<String> errors;

        TokenValidationException(String... errors) {
            this.errors = List.of(errors);
        }

    }

}
