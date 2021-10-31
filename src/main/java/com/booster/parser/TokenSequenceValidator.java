package com.booster.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TokenSequenceValidator {

    public TokenValidationResult validate(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return TokenValidationResult.withErrors(List.of("Err0"));
        }
        Token expectedCommand = eatFrontCommand(tokens);
        if (TokenType.isNotCommand(expectedCommand.getType())) {
            return TokenValidationResult.withErrors(List.of("Err1"));
        }
        if (tokens.size() == 1) {
            return TokenValidationResult.success(tokens);
        }
        List<Token> tokensCopy = new ArrayList<>(tokens.subList(1, tokens.size()));
        if (tokensCopy.size() % 3 != 0) {
            return TokenValidationResult.withErrors(List.of("Err2"));
        }
        for (int i = 0; i < tokensCopy.size(); i += 3) {
            Token flag = tokensCopy.get(i);
            if (flag.getType() != TokenType.FLAG) {
                return TokenValidationResult.withErrors(List.of("Err3"));
            }
            Token separator = tokensCopy.get(i + 1);
            if (separator.getType() != TokenType.SEPARATOR) {
                return TokenValidationResult.withErrors(List.of("Err4"));
            }
            Token value = tokensCopy.get(i + 2);
            if (value.getType() == TokenType.FLAG) {
                return TokenValidationResult.withErrors(List.of("Err5"));
            }
        }
        return TokenValidationResult.success(tokens);
    }

    private Token eatFrontCommand(List<Token> tokens) {
        return tokens.get(0);
    }

}
