package cliclient.parser;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TokenValidationResultTest {

    // todo: nested class for DRY
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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tokens can not be null");
    }

    @Test
    void throwsExceptionWhenNullIsPassedInsteadOfErrors() {
        assertThatThrownBy(() -> TokenValidationResult.withErrors(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("errors can not be null");
    }

}