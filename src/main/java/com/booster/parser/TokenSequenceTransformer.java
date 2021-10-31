package com.booster.parser;

import com.booster.command.Command;

import java.util.List;

public class TokenSequenceTransformer {

    public CommandArguments transform(List<Token> tokens) {
        Token token = tokens.get(0);
        Command command = Command.fromString(token.getValue());
        if (tokens.size() == 1) {
            return CommandArguments.builder().command(command).build();
        }

        var argumentsBuilder = CommandArguments.builder()
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
                case NAME:
                    argumentsBuilder = argumentsBuilder.name(value.getValue());
                    break;
                case DESCRIPTION:
                    argumentsBuilder = argumentsBuilder.description(value.getValue());
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
