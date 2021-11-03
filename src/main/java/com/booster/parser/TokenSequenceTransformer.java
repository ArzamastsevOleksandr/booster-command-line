package com.booster.parser;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class TokenSequenceTransformer {

    public CommandWithArgs transform(List<Token> tokens) {
        Token token = tokens.get(0);
        Command command = Command.fromString(token.getValue());
        if (tokens.size() == 1) {
            return CommandWithArgs.builder().command(command).build();
        }

        var argumentsBuilder = CommandWithArgs.builder()
                .command(command);

        List<Token> copy = tokens.subList(1, tokens.size());

        for (int i = 0; i < copy.size(); i += 3) {
            Token flag = copy.get(i);
            Token value = copy.get(i + 2);

            FlagType flagType = FlagType.fromString(flag.getValue());
            String flagValue = value.getValue();
            switch (flagType) {
                case ID:
                    argumentsBuilder = argumentsBuilder.id(Long.parseLong(flagValue));
                    break;
                case LANGUAGE_ID:
                    argumentsBuilder = argumentsBuilder.languageId(Long.parseLong(flagValue));
                    break;
                case NAME:
                    argumentsBuilder = argumentsBuilder.name(flagValue);
                    break;
                case DESCRIPTION:
                    argumentsBuilder = argumentsBuilder.definition(flagValue);
                    break;
                case FILE:
                    argumentsBuilder = argumentsBuilder.filename(flagValue);
                    break;
                case MODE:
                    argumentsBuilder = argumentsBuilder.mode(flagValue);
                    break;
                case SYNONYMS:
                    argumentsBuilder = argumentsBuilder.synonyms(getWordEquivalentNames(flagValue));
                    break;
                case ANTONYMS:
                    argumentsBuilder = argumentsBuilder.antonyms(getWordEquivalentNames(flagValue));
                    break;
                default:
                    throw new RuntimeException("Flag does not have a handler: " + flagType);
            }
        }
        return argumentsBuilder.build();
    }

    private Set<String> getWordEquivalentNames(String value) {
        return Arrays.stream(value.split(Token.WORD_EQUIVALENT_DELIMITER))
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .collect(toSet());
    }

}
