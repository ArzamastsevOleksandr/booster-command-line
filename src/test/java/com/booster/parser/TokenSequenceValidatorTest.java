package com.booster.parser;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TokenSequenceValidatorTest {

    TokenSequenceValidator validator = new TokenSequenceValidator();

    @Test
    void throwsIAEIfTokensAreNull() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tokens can not be null");
    }

    @Test
    void tokenSequenceMustConsistOfAtLeastOneArgument() {
        TokenValidationResult validationResult = validator.validate(List.of());

        assertThat(validationResult.getTokens()).hasSize(0);
        assertThat(validationResult.getErrors()).containsOnly("Token sequence must consist of at least one argument");
    }

    @Test
    void successForTokenSequenceConsistingOfSingleCommand() {
        Token command = Token.command("h");
        TokenValidationResult validationResult = validator.validate(List.of(command));

        assertThat(validationResult.getErrors()).hasSize(0);
        assertThat(validationResult.getTokens()).containsOnly(command);
    }

    @Test
    void failureForTokenSequenceConsistingOfSingleNonCommandToken() {
        Token separator = Token.separator();
        TokenValidationResult validationResult = validator.validate(List.of(separator));

        assertThat(validationResult.getErrors()).containsOnly("Token sequence must start with a command");
        assertThat(validationResult.getTokens()).hasSize(0);
    }

    @Test
    void invalidArgumentSequenceIsDetected() {
        Token command = Token.command("h");
        Token flag = Token.flag("n");
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> cmdFlag = List.of(command, flag);
        List<Token> cmdFlagSep = List.of(command, flag, separator);
        List<Token> cmdFlagSepTxtFlag = List.of(command, flag, separator, text, flag);

        var softAssertions = new SoftAssertions();

        Stream.of(cmdFlag, cmdFlagSep, cmdFlagSepTxtFlag)
                .forEach(tokens -> {
                    TokenValidationResult validationResult = validator.validate(tokens);

                    softAssertions.assertThat(validationResult.getTokens())
                            .withFailMessage(() -> "expected no tokens, got: " + validationResult.getTokens())
                            .hasSize(0);

                    softAssertions.assertThat(validationResult.getErrors())
                            .containsOnly("Arguments must follow a pattern of flag -> separator -> value");

                });
        softAssertions.assertAll();
    }

    @Test
    void expectedFlagGotCommand() {
        Token command = Token.command("h");
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, command, separator, text);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.getTokens()).hasSize(0);

        assertThat(validationResult.getErrors())
                .containsOnly("Expected flag, got: " + command.getValue() + " with type: " + tokenTypeToLowerCaseString(command));
    }

    @Test
    void expectsSeparator() {
        Token command = Token.command("h");
        Token flag = Token.flag("n");
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, command, text);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.getTokens()).hasSize(0);

        assertThat(validationResult.getErrors())
                .containsOnly("Expected separator, got: " + command.getValue() + " with type: " + tokenTypeToLowerCaseString(command));
    }

    @Test
    void doesNotExpectFlagAfterSeparator() {
        Token command = Token.command("h");
        Token flag = Token.flag("n");
        Token separator = Token.separator();

        List<Token> tokens = List.of(command, flag, separator, flag);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.getTokens()).hasSize(0);

        assertThat(validationResult.getErrors())
                .containsOnly("Separator is followed by a flag: " + flag.getValue());
    }

    @Test
    void idMustBePositiveLongNumber() {
        Token command = Token.command("h");
        Token flag = Token.flag(FlagType.ID.value);
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, separator, text);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.getTokens()).hasSize(0);

        assertThat(validationResult.getErrors())
                .containsOnly("Id argument must be a positive long number, got: " + text.getValue());
    }

    @Test
    void languageIdMustBePositiveLongNumber() {
        Token command = Token.command("h");
        Token flag = Token.flag(FlagType.LANGUAGE_ID.value);
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, separator, text);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.getTokens()).hasSize(0);

        assertThat(validationResult.getErrors())
                .containsOnly("Language id argument must be a positive long number, got: " + text.getValue());
    }

    @Test
    void successPath() {
        Token command = Token.command("h");
        Token flag = Token.flag(FlagType.LANGUAGE_ID.value);
        Token separator = Token.separator();
        Token number = Token.number("1");

        List<Token> tokens = List.of(command, flag, separator, number);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.getErrors()).hasSize(0);
        assertThat(validationResult.getTokens()).containsExactly(command, flag, separator, number);
    }

    private String tokenTypeToLowerCaseString(Token token) {
        return token.getType().toString().toLowerCase();
    }

}