package com.booster.parser;

import lombok.Value;

@Value
public class Token {

    String value;
    TokenType type;

    public static Token number(String value) {
        return new Token(value, TokenType.NUMBER);
    }

    public static Token text(String value) {
        return new Token(value, TokenType.TEXT);
    }

    public static Token separator() {
        return new Token("=", TokenType.SEPARATOR);
    }

    public static Token command(String value) {
        return new Token(value, TokenType.COMMAND);
    }

    public static Token flag(String value) {
        return new Token(value, TokenType.FLAG);
    }

}
