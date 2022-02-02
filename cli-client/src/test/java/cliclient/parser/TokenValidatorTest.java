package cliclient.parser;

import cliclient.command.FlagType;
import cliclient.util.NumberUtil;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenValidatorTest {

    @Mock
    NumberUtil numberUtil;

    @InjectMocks
    TokenValidator validator;

    @Test
    void tokenSequenceMustConsistOfAtLeastOneArgument() {
        TokenValidationResult validationResult = validator.validate(List.of());

        assertThat(validationResult.tokens()).hasSize(0);
        assertThat(validationResult.errors()).containsOnly("No input");
    }

    @Test
    void successForTokenSequenceConsistingOfSingleCommand() {
        Token command = Token.command("h");
        TokenValidationResult validationResult = validator.validate(List.of(command));

        assertThat(validationResult.errors()).hasSize(0);
        assertThat(validationResult.tokens()).containsOnly(command);
    }

    @Test
    void failureForTokenSequenceConsistingOfSingleNonCommandToken() {
        Token separator = Token.separator();
        TokenValidationResult validationResult = validator.validate(List.of(separator));

        assertThat(validationResult.errors()).containsOnly("Sequence must start with a command");
        assertThat(validationResult.tokens()).hasSize(0);
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

                    softAssertions.assertThat(validationResult.tokens())
                            .withFailMessage(() -> "expected no tokens, got: " + validationResult.tokens())
                            .hasSize(0);

                    softAssertions.assertThat(validationResult.errors())
                            .containsOnly("Arguments must follow a pattern of [flag separator value]");

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

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Expected flag, got: " + command.value() + " with type: " + tokenTypeToLowerCaseString(command));
    }

    @Test
    void expectsSeparator() {
        Token command = Token.command("h");
        Token flag = Token.flag("n");
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, command, text);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Expected separator, got: " + command.value() + " with type: " + tokenTypeToLowerCaseString(command));
    }

    @Test
    void doesNotExpectFlagAfterSeparator() {
        Token command = Token.command("h");
        Token flag = Token.flag("n");
        Token separator = Token.separator();

        List<Token> tokens = List.of(command, flag, separator, flag);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Separator is followed by a flag: " + flag.value());
    }

    @Test
    void idMustBePositiveLongNumber() {
        Token command = Token.command("h");
        Token flag = Token.flag("id");
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, separator, text);

        when(numberUtil.isNotPositiveLong(text.value())).thenReturn(true);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Id argument must be a positive long number, got: " + text.value());
    }

    @Test
    void languageIdMustBePositiveLongNumber() {
        Token command = Token.command("h");
        Token flag = Token.flag("lid");
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, separator, text);

        when(numberUtil.isNotPositiveLong(text.value())).thenReturn(true);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Language id argument must be a positive long number, got: " + text.value());
    }

    @Test
    void correctAnswersCountMustBePositiveIntegerNumber() {
        Token command = Token.command("h");
        Token flag = Token.flag("cac");
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, separator, text);

        when(numberUtil.isNotPositiveInteger(text.value())).thenReturn(true);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Correct answers count argument must be a positive integer number, got: " + text.value());
    }

    @Test
    void paginationMustBePositiveIntegerNumber() {
        Token command = Token.command("ve");
        Token flag = Token.flag("pg");
        Token separator = Token.separator();
        Token text = Token.text("a");

        List<Token> tokens = List.of(command, flag, separator, text);

        when(numberUtil.isNotPositiveInteger(text.value())).thenReturn(true);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.tokens()).hasSize(0);

        assertThat(validationResult.errors())
                .containsOnly("Pagination argument must be a positive integer number, got: " + text.value());
    }

    @Test
    void successPath() {
        Token command = Token.command("h");
        Token flag = Token.flag(FlagType.LANGUAGE_ID.value);
        Token separator = Token.separator();
        Token number = Token.number("1");

        List<Token> tokens = List.of(command, flag, separator, number);
        TokenValidationResult validationResult = validator.validate(tokens);

        assertThat(validationResult.errors()).hasSize(0);
        assertThat(validationResult.tokens()).containsExactly(command, flag, separator, number);
    }

    private String tokenTypeToLowerCaseString(Token token) {
        return token.type().toString().toLowerCase();
    }

}