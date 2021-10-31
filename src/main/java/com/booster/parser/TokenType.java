package com.booster.parser;

public enum TokenType {
    COMMAND,
    TEXT,
    NUMBER,
    SEPARATOR,
    FLAG;

    public static boolean isNotCommand(TokenType tokenType) {
        return tokenType != COMMAND;
    }

}
