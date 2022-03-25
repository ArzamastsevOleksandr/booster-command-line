package cliclient.parser;

import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import cliclient.util.CollectionUtils;
import cliclient.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
class CommandWithArgsService {

    private final NumberUtil numberUtil;

    CommandWithArgs toCommandWithArgs(List<Token> tokens) {
        try {
            if (tokens.isEmpty()) {
                return CommandWithArgs.builder().command(Command.NO_INPUT).build();
            }
            validateFirstToken(tokens);
            var commandWithArgsBuilder = CommandWithArgs.builder()
                    .command(Command.fromString(tokens.get(0).value()));
            if (tokens.size() == 1) {
                return commandWithArgsBuilder.build();
            }
            if (tokens.size() == 2) {
                checkSequenceIsHelpOnCommand(tokens);
                return commandWithArgsBuilder
                        .helpTarget(Command.fromString(tokens.get(1).value()))
                        .build();
            }
            List<Token> commandArguments = CollectionUtils.sublist(tokens, 1);
            checkCommandArgumentsSize(commandArguments);

            validateCommandArguments(commandArguments);

            for (int i = 0; i < commandArguments.size(); i += 3) {
                Token flag = commandArguments.get(i);
                Token value = commandArguments.get(i + 2);

                FlagType flagType = FlagType.fromString(flag.value());
                String flagValue = value.value();
                commandWithArgsBuilder = switch (flagType) {
                    case ID -> commandWithArgsBuilder.id(Long.parseLong(flagValue));
                    case NOTE_ID -> commandWithArgsBuilder.noteId(Long.parseLong(flagValue));
                    case VOCABULARY_ENTRY_ID -> commandWithArgsBuilder.vocabularyEntryId(Long.parseLong(flagValue));
                    case NAME -> commandWithArgsBuilder.name(flagValue);
                    case LANGUAGE_NAME -> commandWithArgsBuilder.languageName(flagValue);
                    case TAG -> commandWithArgsBuilder.tag(flagValue);
                    case DEFINITION -> commandWithArgsBuilder.definition(flagValue);
                    case FILE -> commandWithArgsBuilder.filename(flagValue);
                    case MODE_VOCABULARY -> commandWithArgsBuilder.mode(VocabularyTrainingSessionMode.fromString(flagValue));
                    case SYNONYMS -> commandWithArgsBuilder.synonyms(getWordEquivalentNames(flagValue));
                    case ANTONYMS -> commandWithArgsBuilder.antonyms(getWordEquivalentNames(flagValue));
                    case CONTENT -> commandWithArgsBuilder.content(flagValue);
                    case CORRECT_ANSWERS_COUNT -> commandWithArgsBuilder.correctAnswersCount(Integer.parseInt(flagValue));
                    case ADD_ANTONYMS -> commandWithArgsBuilder.addAntonyms(getWordEquivalentNames(flagValue));
                    case ADD_SYNONYMS -> commandWithArgsBuilder.addSynonyms(getWordEquivalentNames(flagValue));
                    case REMOVE_ANTONYMS -> commandWithArgsBuilder.removeAntonyms(getWordEquivalentNames(flagValue));
                    case REMOVE_SYNONYMS -> commandWithArgsBuilder.removeSynonyms(getWordEquivalentNames(flagValue));
                    case PAGINATION -> commandWithArgsBuilder.pagination(Integer.parseInt(flagValue));
                    case LANGUAGES_PAGINATION -> commandWithArgsBuilder.languagesPagination(Integer.parseInt(flagValue));
                    case NOTES_PAGINATION -> commandWithArgsBuilder.notesPagination(Integer.parseInt(flagValue));
                    case TAGS_PAGINATION -> commandWithArgsBuilder.tagsPagination(Integer.parseInt(flagValue));
                    case VOCABULARY_PAGINATION -> commandWithArgsBuilder.vocabularyPagination(Integer.parseInt(flagValue));
                    case SUBSTRING -> commandWithArgsBuilder.substring(flagValue);
                    case CONTEXTS -> commandWithArgsBuilder.contexts(getContexts(flagValue));
                    case ENTRIES_PER_VOCABULARY_TRAINING_SESSION -> commandWithArgsBuilder.entriesPerVocabularyTrainingSession(Integer.parseInt(flagValue));
                    default -> throw new RuntimeException("Flag does not have a handler: " + flagType);
                };
            }
            return commandWithArgsBuilder.build();
        } catch (TokenValidationException e) {
            return CommandWithArgs.withErrors(e.errors);
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
        if (help != Command.HELP) {
            throw new TokenValidationException("Only " + Command.HELP + " command can consist of 2 arguments");
        }
        if (Token.isNotCommand(tokens.get(1))) {
            throw new TokenValidationException("Help works with commands, got: " + tokens.get(1).value());
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

    private Set<String> getContexts(String value) {
        return Arrays.stream(value.split(Token.CONTEXT_DELIMITER))
                .map(String::strip)
                .filter(this::isNotBlank)
                .collect(toSet());
    }

    private Set<String> getWordEquivalentNames(String value) {
        return Arrays.stream(value.split(Token.WORD_EQUIVALENT_DELIMITER))
                .map(String::strip)
                .filter(this::isNotBlank)
                .collect(toSet());
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

}
