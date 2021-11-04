package com.booster.parser;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TokenTest {

    static final String VALUE = "123";
    static final String SEPARATOR = "=";

    @Test
    void returnsTokenWithNumberType() {
        assertThatTokenHasValueAndType(Token.number(VALUE), VALUE, TokenType.NUMBER);
    }

    @Test
    void returnsTokenWithTextType() {
        assertThatTokenHasValueAndType(Token.text(VALUE), VALUE, TokenType.TEXT);
    }

    @Test
    void returnsTokenWithCommandType() {
        assertThatTokenHasValueAndType(Token.command(VALUE), VALUE, TokenType.COMMAND);
    }

    @Test
    void returnsTokenWithFlagType() {
        assertThatTokenHasValueAndType(Token.flag(VALUE), VALUE, TokenType.FLAG);
    }

    @Test
    void returnsTokenWithSeparatorType() {
        assertThatTokenHasValueAndType(Token.separator(), SEPARATOR, TokenType.SEPARATOR);
    }

    @Test
    void throwsIAEIfTextIsNull() {
        assertThatThrownBy(() -> Token.text(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("text value can not be null");
    }

    @Test
    void throwsIAEIfNumberIsNull() {
        assertThatThrownBy(() -> Token.number(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("number value can not be null");
    }

    @Test
    void throwsIAEIfFlagIsNull() {
        assertThatThrownBy(() -> Token.flag(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("flag value can not be null");
    }

    @Test
    void throwsIAEIfCommandIsNull() {
        assertThatThrownBy(() -> Token.command(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("command value can not be null");
    }

    private void assertThatTokenHasValueAndType(Token token, String value, TokenType number) {
        assertThat(token)
                .hasFieldOrPropertyWithValue("type", number)
                .hasFieldOrPropertyWithValue("value", value);
    }

}