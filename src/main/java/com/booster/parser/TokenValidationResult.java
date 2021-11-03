package com.booster.parser;

import lombok.Value;

import java.util.List;
import java.util.Objects;

@Value
public class TokenValidationResult {

    List<Token> tokens;
    List<String> errors;

    private TokenValidationResult(List<Token> tokens, List<String> errors) {
        this.tokens = tokens;
        this.errors = errors;
    }

    public boolean hasNoErrors() {
        return errors.size() == 0;
    }

    public static TokenValidationResult withErrors(List<String> errors) {
        return new TokenValidationResult(List.of(), Objects.requireNonNull(errors));
    }

    public static TokenValidationResult success(List<Token> tokens) {
        return new TokenValidationResult(Objects.requireNonNull(tokens), List.of());
    }

}
