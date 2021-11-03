package com.booster.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TokenValidationResultTest {

    @Test
    void createsSuccessResultWithTokensAndNoErrors() {
        List<Token> tokens = List.of(Token.separator());
        TokenValidationResult success = TokenValidationResult.success(tokens);

        assertThat(success)
                .hasFieldOrPropertyWithValue("tokens", tokens)
                .hasFieldOrPropertyWithValue("errors", List.of());
    }

    @Test
    void createsResultWithErrorsAndNoTokens() {
        List<String> errors = List.of("Error");
        TokenValidationResult withErrors = TokenValidationResult.withErrors(errors);

        assertThat(withErrors)
                .hasFieldOrPropertyWithValue("tokens", List.of())
                .hasFieldOrPropertyWithValue("errors", errors);
    }

    @Test
    void throwsExceptionWhenNullIsPassedInsteadOfTokens() {
        assertThatThrownBy(() -> TokenValidationResult.success(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void throwsExceptionWhenNullIsPassedInsteadOfErrors() {
        assertThatThrownBy(() -> TokenValidationResult.withErrors(null))
                .isInstanceOf(NullPointerException.class);
    }

}