package com.booster.parser;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TokenSequenceTransformer {

    public CommandWithArguments transform(List<Token> tokens) {
        Token token = tokens.get(0);
        Command command = Command.fromString(token.getValue());
        if (tokens.size() == 1) {
            return CommandWithArguments.builder().command(command).build();
        }

        var argumentsBuilder = CommandWithArguments.builder()
                .command(command);

        List<Token> copy = tokens.subList(1, tokens.size());

        for (int i = 0; i < copy.size(); i += 3) {
            Token flag = copy.get(i);
            Token value = copy.get(i + 2);

            FlagType flagType = FlagType.fromString(flag.getValue());
            switch (flagType) {
                case ID:
                    argumentsBuilder = argumentsBuilder.id(Long.parseLong(value.getValue()));
                    break;
                case LANGUAGE_ID:
                    argumentsBuilder = argumentsBuilder.languageId(Long.parseLong(value.getValue()));
                    break;
                case NAME:
                    argumentsBuilder = argumentsBuilder.name(value.getValue());
                    break;
                case DESCRIPTION:
                    argumentsBuilder = argumentsBuilder.definition(value.getValue());
                    break;
                case FILE:
                    argumentsBuilder = argumentsBuilder.filename(value.getValue());
                    break;
                case SYNONYMS:
                case ANTONYMS:
                    // todo: process synonyms, antonyms, training session modes
                default:
                    throw new RuntimeException("Must never happen");
            }
        }
        return argumentsBuilder.build();
    }

}
