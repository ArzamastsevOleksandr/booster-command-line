package com.booster.parser;

import com.booster.command.Command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class LexicalAnalyzer {
    // "   ave \n= n  \s =s  \a=a "
    // COMMAND, FLAG, =, TEXT, FLAG, =, TEXT, FLAG, =, TEXT
    // COMMAND, NAME_VALUE, SYNONYM_VALUE, ANTONYM_VALUE
    public List<Token> parse(String input) {
        return parse(input.toCharArray(), new LinkedList<>());
    }

    private List<Token> parse(char[] chars, List<Token> tokens) {
        if (chars.length == 0) {
            return tokens;
        }
        return parse(eatFrontChunk(chars, tokens), tokens);
    }

    private char[] eatFrontChunk(char[] chars, List<Token> tokens) {
        char firstChar = chars[0];
        if (Character.isWhitespace(firstChar)) {
            return eatFrontWhitespaces(chars);
        } else if (Character.isLetter(firstChar)) {
            return eatFrontLettersAndDigitsAndWhitespaces(chars, tokens);
        } else if (isFlagMarker(firstChar)) {
            return eatFrontFlag(chars, tokens);
        } else if (isSeparator(firstChar)) {
            return eatFrontSeparator(chars, tokens);
        } else if (Character.isDigit(firstChar)) {
            return eatFrontDigitsAndLettersAndMarkersAndSeparators(chars, tokens);
        }
        // deliberately return an empty array to avoid SOE
        return new char[]{};
    }

    private char[] eatFrontWhitespaces(char[] chars) {
        int i = 0;
        while (i < chars.length && Character.isWhitespace(chars[i])) {
            ++i;
        }
        return Arrays.copyOfRange(chars, i, chars.length);
    }

    private char[] eatFrontDigitsAndLettersAndMarkersAndSeparators(char[] chars, List<Token> tokens) {
        int i = 0;
        var sb = new StringBuilder();
        while (i < chars.length && (Character.isLetterOrDigit(chars[i]) || isFlagMarker(chars[i]) || isSeparator(chars[i]))) {
            sb.append(chars[i++]);
        }
        if (isValidPositiveLongNumber(sb.toString())) {
            tokens.add(Token.number(sb.toString()));
        } else {
            tokens.add(Token.text(sb.toString()));
        }
        return Arrays.copyOfRange(chars, i, chars.length);
    }

    private boolean isValidPositiveLongNumber(String toString) {
        try {
            return Long.parseLong(toString) > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private char[] eatFrontSeparator(char[] chars, List<Token> tokens) {
        tokens.add(Token.separator());
        return Arrays.copyOfRange(chars, 1, chars.length);
    }

    private boolean isSeparator(char character) {
        return character == '=';
    }

    private char[] eatFrontFlag(char[] chars, List<Token> tokens) {
        int i = 0;
        var sb = new StringBuilder();
        while (i < chars.length && (Character.isLetterOrDigit(chars[i]) || chars[i] == '\\')) {
            sb.append(chars[i++]);
        }
        addFlagOrText(tokens, sb);
        return Arrays.copyOfRange(chars, i, chars.length);
    }

    private void addFlagOrText(List<Token> tokens, StringBuilder sb) {
        if (FlagType.isKnown(FlagType.fromString(sb.substring(1)))) {
            tokens.add(Token.flag(sb.substring(1)));
        } else {
            tokens.add(Token.text(sb.toString()));
        }
    }

    private boolean isFlagMarker(char character) {
        return character == '\\';
    }

    private char[] eatFrontLettersAndDigitsAndWhitespaces(char[] chars, List<Token> tokens) {
        int i = 0;
        var sb = new StringBuilder();
        while (i < chars.length && (Character.isLetterOrDigit(chars[i]) || Character.isWhitespace(chars[i]))) {
            sb.append(chars[i++]);
        }
        addCommandOrText(tokens, sb);
        return Arrays.copyOfRange(chars, i, chars.length);
    }

    private void addCommandOrText(List<Token> tokens, StringBuilder sb) {
        String str = sb.toString().strip();
        if (Command.isRecognizable(Command.fromString(str))) {
            tokens.add(Token.command(str));
        } else {
            tokens.add(Token.text(str));
        }
    }

}
