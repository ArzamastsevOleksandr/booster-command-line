package cliclient.parser;

import lombok.Value;

import java.util.List;

@Value
class TokenValidationResult {

    List<Token> tokens;
    List<String> errors;

    private TokenValidationResult(List<Token> tokens, List<String> errors) {
        this.tokens = tokens;
        this.errors = errors;
    }

    boolean hasNoErrors() {
        return errors.size() == 0;
    }

    // todo: Supplier for string concatenation?
    // todo: var arg
    static TokenValidationResult withErrors(List<String> errors) {
        return new TokenValidationResult(List.of(), errors);
    }

    static TokenValidationResult success(List<Token> tokens) {
        return new TokenValidationResult(tokens, List.of());
    }

}
