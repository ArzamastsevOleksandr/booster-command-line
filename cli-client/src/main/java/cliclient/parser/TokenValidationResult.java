package cliclient.parser;

import java.util.List;

record TokenValidationResult(List<Token> tokens, List<String> errors) {

    boolean hasErrors() {
        return errors.size() != 0;
    }

    boolean hasNoTokens() {
        return tokens.isEmpty();
    }

    static TokenValidationResult withErrors(List<String> errors) {
        return new TokenValidationResult(List.of(), errors);
    }

    static TokenValidationResult success(List<Token> tokens) {
        return new TokenValidationResult(tokens, List.of());
    }

    static TokenValidationResult empty() {
        return new TokenValidationResult(List.of(), List.of());
    }

}
