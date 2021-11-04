package com.booster.parser;

import com.booster.util.ObjectUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class TokenSequenceValidator {

    // todo: implement support for flags with no values
    TokenValidationResult validate(List<Token> tokens) {
        ObjectUtil.requireNonNullOrElseThrowIAE(tokens, "tokens can not be null");
        if (tokens.isEmpty()) {
            return TokenValidationResult.withErrors(List.of("Token sequence must consist of at least one argument"));
        }
        Token expectedCommand = eatFrontCommand(tokens);
        if (TokenType.isNotCommand(expectedCommand.getType())) {
            return TokenValidationResult.withErrors(List.of("Token sequence must start with a command"));
        }
        if (tokens.size() == 1) {
            return TokenValidationResult.success(tokens);
        }
        List<Token> tokensCopy = new ArrayList<>(tokens.subList(1, tokens.size()));
        if (tokensCopy.size() % 3 != 0) {
            return TokenValidationResult.withErrors(List.of("Arguments must follow a pattern of flag -> separator -> value"));
        }
        for (int i = 0; i < tokensCopy.size(); i += 3) {
            Token flag = tokensCopy.get(i);
            if (flag.getType() != TokenType.FLAG) {
                return TokenValidationResult.withErrors(List.of("Expected flag, got: " + flag.getValue() + " with type: " + tokenTypeToLowerCaseString(flag)));
            }
            Token separator = tokensCopy.get(i + 1);
            if (separator.getType() != TokenType.SEPARATOR) {
                return TokenValidationResult.withErrors(List.of("Expected separator, got: " + separator.getValue() + " with type: " + tokenTypeToLowerCaseString(separator)));
            }
            Token value = tokensCopy.get(i + 2);
            if (value.getType() == TokenType.FLAG) {
                return TokenValidationResult.withErrors(List.of("Separator is followed by a flag: " + value.getValue()));
            }
        }
        return TokenValidationResult.success(tokens);
    }

    private String tokenTypeToLowerCaseString(Token flag) {
        return flag.getType().toString().toLowerCase();
    }

    private Token eatFrontCommand(List<Token> tokens) {
        return tokens.get(0);
    }

}
