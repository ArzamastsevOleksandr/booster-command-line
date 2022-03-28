package cliclient.parser;

import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
class CommandWithArgsService {

    CommandWithArgs toCommandWithArgs(List<Token> tokens, ValidationResult validationResult) {
        return switch (validationResult.commandValidationResult) {
            case NO_INPUT -> CommandWithArgs.builder()
                    .command(Command.NO_INPUT)
                    .build();
            case MUST_START_WITH_COMMAND -> CommandWithArgs.builder()
                    .errors(List.of("Input must start with command, got: " + validationResult.comment))
                    .build();
            case SINGLE_COMMAND, COMMAND_MUST_BE_FOLLOWED_BY_FLAG_ARGUMENT_PAIRS -> CommandWithArgs.builder()
                    .command(Command.fromString(tokens.get(0).value()))
                    .build();
            case HELP_ON_COMMAND -> CommandWithArgs.builder()
                    .command(Command.HELP)
                    .helpTarget(Command.fromString(tokens.get(1).value()))
                    .build();
            case HELP_ON_TEXT -> CommandWithArgs.builder()
                    .command(Command.HELP)
                    .errors(List.of("Help used for unknown command: " + validationResult.comment))
                    .build();
            case HELP_EXPECTED_BUT_GOT_OTHER_COMMAND_INSTEAD -> CommandWithArgs.builder()
                    .command(Command.fromString(tokens.get(0).value()))
                    .errors(List.of("Command must be followed by \\flag=value pairs"))
                    .build();
            case FORBIDDEN_FLAG_VALUE -> CommandWithArgs.builder()
                    .command(Command.fromString(tokens.get(0).value()))
                    .errors(List.of(validationResult.comment))
                    .build();
            case COMMAND_WITH_FLAG_VALUE_PAIRS -> constructCommandWithArgs(tokens);
        };
    }

    private CommandWithArgs constructCommandWithArgs(List<Token> tokens) {
        var commandWithArgsBuilder = CommandWithArgs.builder().command(Command.fromString(tokens.get(0).value()));
        List<Token> flagValuePairs = tokens.subList(1, tokens.size());
        for (int i = 0; i < flagValuePairs.size(); i += 3) {
            Token flag = flagValuePairs.get(i);
            Token value = flagValuePairs.get(i + 2);

            var flagType = FlagType.fromString(flag.value());
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
