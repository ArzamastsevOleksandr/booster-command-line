package com.booster.parser;

enum TokenType {
    COMMAND,
    TEXT,
    NUMBER,
    SEPARATOR,
    FLAG;

    static boolean isNotCommand(TokenType tokenType) {
        return tokenType != COMMAND;
    }

}
