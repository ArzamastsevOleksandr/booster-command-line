package cliclient.parser;

import lombok.Value;

@Value
class Token {

    static final String SEPARATOR = "=";
    static final String FLAG_MARKER = "\\";
    static final String WORD_EQUIVALENT_DELIMITER = ";";
    static final String CONTEXT_DELIMITER = "/";

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

    static boolean isFlag(Token token) {
        return !isNotFlag(token);
    }

    static boolean isNotFlag(Token token) {
        return token.getType() != TokenType.FLAG;
    }

    static boolean isNotCommand(Token token) {
        return token.getType() != TokenType.COMMAND;
    }

    static boolean isNotSeparator(Token token) {
        return token.getType() != TokenType.SEPARATOR;
    }

}
