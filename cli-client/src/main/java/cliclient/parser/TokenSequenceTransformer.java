package cliclient.parser;

import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.command.arguments.VocabularyTrainingSessionMode;
import cliclient.util.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
class TokenSequenceTransformer {

    CommandWithArgs transform(TokenValidationResult tokenValidationResult) {
        if (tokenValidationResult.hasErrors()) {
            return CommandWithArgs.withErrors(tokenValidationResult.errors());
        }
        if (tokenValidationResult.hasNoTokens()) {
            return CommandWithArgs.builder().command(Command.NO_INPUT).build();
        }
        List<Token> tokens = tokenValidationResult.tokens();
        Token token = tokens.get(0);
        var command = Command.fromString(token.value());
        var argumentsBuilder = CommandWithArgs.builder().command(command);
        if (tokens.size() == 1) {
            return argumentsBuilder.build();
        }
        List<Token> copy = CollectionUtils.sublist(tokens, 1);

        for (int i = 0; i < copy.size(); i += 3) {
            Token flag = copy.get(i);
            Token value = copy.get(i + 2);

            FlagType flagType = FlagType.fromString(flag.value());
            String flagValue = value.value();
            argumentsBuilder = switch (flagType) {
                case ID -> argumentsBuilder.id(Long.parseLong(flagValue));
                case LANGUAGE_ID -> argumentsBuilder.languageId(Long.parseLong(flagValue));
                case NOTE_ID -> argumentsBuilder.noteId(Long.parseLong(flagValue));
                case VOCABULARY_ENTRY_ID -> argumentsBuilder.vocabularyEntryId(Long.parseLong(flagValue));
                case NAME -> argumentsBuilder.name(flagValue);
                case TAG -> argumentsBuilder.tag(flagValue);
                case DEFINITION -> argumentsBuilder.definition(flagValue);
                case FILE -> argumentsBuilder.filename(flagValue);
                case MODE_VOCABULARY -> argumentsBuilder.mode(VocabularyTrainingSessionMode.fromString(flagValue));
                case SYNONYMS -> argumentsBuilder.synonyms(getWordEquivalentNames(flagValue));
                case ANTONYMS -> argumentsBuilder.antonyms(getWordEquivalentNames(flagValue));
                case CONTENT -> argumentsBuilder.content(flagValue);
                case CORRECT_ANSWERS_COUNT -> argumentsBuilder.correctAnswersCount(Integer.parseInt(flagValue));
                case ADD_ANTONYMS -> argumentsBuilder.addAntonyms(getWordEquivalentNames(flagValue));
                case ADD_SYNONYMS -> argumentsBuilder.addSynonyms(getWordEquivalentNames(flagValue));
                case REMOVE_ANTONYMS -> argumentsBuilder.removeAntonyms(getWordEquivalentNames(flagValue));
                case REMOVE_SYNONYMS -> argumentsBuilder.removeSynonyms(getWordEquivalentNames(flagValue));
                case PAGINATION -> argumentsBuilder.pagination(Integer.parseInt(flagValue));
                case SUBSTRING -> argumentsBuilder.substring(flagValue);
                case CONTEXTS -> argumentsBuilder.contexts(getContexts(flagValue));
                case VOCABULARY_TRAINING_SESSION_SIZE -> argumentsBuilder.vocabularyTrainingSessionSize(Integer.parseInt(flagValue));
                default -> throw new RuntimeException("Flag does not have a handler: " + flagType);
            };
        }
        return argumentsBuilder.build();
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
