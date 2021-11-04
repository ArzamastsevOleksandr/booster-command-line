package com.booster.parser;

import lombok.Value;

@Value
class Token {

    static final String SEPARATOR = "=";
    static final String FLAG_MARKER = "\\";
    static final String WORD_EQUIVALENT_DELIMITER = ";";

    String value;
    TokenType type;

    static Token number(String value) {
        return new Token(value, TokenType.NUMBER);
    }

    static Token text(String value) {
        return new Token(value, TokenType.TEXT);
    }

    static Token separator() {
        return new Token(SEPARATOR, TokenType.SEPARATOR);
    }

    static Token command(String value) {
        return new Token(value, TokenType.COMMAND);
    }

    static Token flag(String value) {
        return new Token(value, TokenType.FLAG);
    }

}
