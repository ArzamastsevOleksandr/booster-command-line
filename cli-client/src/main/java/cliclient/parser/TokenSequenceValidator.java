package cliclient.parser;

import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.args.VocabularyTrainingSessionMode;
import cliclient.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static cliclient.parser.CommandValidationResult.*;

@Component
@RequiredArgsConstructor
public class TokenSequenceValidator {

    private final NumberUtil numberUtil;

    ValidationResult validate(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return new ValidationResult(NO_INPUT);
        }
        Token first = tokens.get(0);
        if (Token.isNotCommand(first)) {
            return new ValidationResult(MUST_START_WITH_COMMAND, first.value());
        }
        if (tokens.size() == 1) {
            return new ValidationResult(SINGLE_COMMAND, Command.fromString(first.value()));
        }
        var command = Command.fromString(first.value());
        if (tokens.size() == 2) {
            return validateHelpCommand(tokens, command);
        }
        List<Token> commandArguments = tokens.subList(1, tokens.size());
        if (commandArguments.size() % 3 != 0) {
            return new ValidationResult(COMMAND_MUST_BE_FOLLOWED_BY_FLAG_ARGUMENT_PAIRS, command);
        }
        try {
            validateCommandArguments(commandArguments);
        } catch (TokenValidationException e) {
            return new ValidationResult(FORBIDDEN_FLAG_VALUE, command, e.error);
        }
        return new ValidationResult(COMMAND_WITH_FLAG_VALUE_PAIRS);
    }

    private ValidationResult validateHelpCommand(List<Token> tokens, Command command) {
        String secondTokenValue = tokens.get(1).value();
        Command secondCommand = Command.fromString(secondTokenValue);
        if (command == Command.HELP && Command.isRecognizable(secondCommand)) {
            return new ValidationResult(HELP_ON_COMMAND, secondCommand);
        } else if (command == Command.HELP && !Command.isRecognizable(secondCommand)) {
            return new ValidationResult(HELP_ON_TEXT, secondTokenValue);
        } else {
            return new ValidationResult(HELP_EXPECTED_BUT_GOT_OTHER_COMMAND_INSTEAD, command);
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

    private String tokenTypeToLowerCase(Token token) {
        return token.type().toString().toLowerCase();
    }

    private void checkVocabularyTrainingSessionModeIsCorrect(String value) {
        if (VocabularyTrainingSessionMode.isUnrecognized(value)) {
            throw new TokenValidationException("""
                    Unrecognized training session mode: %s
                    Available modes are: %s
                    """.formatted(value, VocabularyTrainingSessionMode.modesToString())
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

    @RequiredArgsConstructor
    private static class TokenValidationException extends RuntimeException {
        final String error;
    }

}
