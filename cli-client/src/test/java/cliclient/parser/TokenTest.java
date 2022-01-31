package cliclient.parser;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class TokenTest {

    static final String VALUE = "123";
    static final String SEPARATOR_VALUE = "=";

    static final Token SEPARATOR_TOKEN = Token.separator();
    static final Token NUMBER_TOKEN = Token.number("1");
    static final Token TEXT_TOKEN = Token.text("words");
    static final Token FLAG_TOKEN = Token.flag("n");
    static final Token COMMAND_TOKEN = Token.command("l");

    @Nested
    class ReturnsTokenWithType {

        @Test
        void number() {
            assertThatTokenHasValueAndType(Token.number(VALUE), VALUE, TokenType.NUMBER);
        }

        @Test
        void text() {
            assertThatTokenHasValueAndType(Token.text(VALUE), VALUE, TokenType.TEXT);
        }

        @Test
        void command() {
            assertThatTokenHasValueAndType(Token.command(VALUE), VALUE, TokenType.COMMAND);
        }

        @Test
        void flag() {
            assertThatTokenHasValueAndType(Token.flag(VALUE), VALUE, TokenType.FLAG);
        }

        @Test
        void separator() {
            assertThatTokenHasValueAndType(Token.separator(), SEPARATOR_VALUE, TokenType.SEPARATOR);
        }

    }

    @Test
    void isFlag() {
        var softAssertions = new SoftAssertions();

        Stream.of(SEPARATOR_TOKEN, NUMBER_TOKEN, TEXT_TOKEN, COMMAND_TOKEN)
                .forEach(token -> softAssertions.assertThat(Token.isFlag(token))
                        .withFailMessage(() -> "expected not flag, got: " + token)
                        .isFalse()
                );

        softAssertions.assertThat(Token.isFlag(FLAG_TOKEN))
                .withFailMessage(() -> "expected flag, got: " + FLAG_TOKEN)
                .isTrue();

        softAssertions.assertAll();
    }

    @Test
    void isNotFlag() {
        var softAssertions = new SoftAssertions();

        Stream.of(SEPARATOR_TOKEN, NUMBER_TOKEN, TEXT_TOKEN, COMMAND_TOKEN)
                .forEach(token -> softAssertions.assertThat(Token.isNotFlag(token))
                        .withFailMessage(() -> "expected not flag, got: " + token)
                        .isTrue()
                );

        softAssertions.assertThat(Token.isNotFlag(FLAG_TOKEN))
                .withFailMessage(() -> "expected flag, got: " + FLAG_TOKEN)
                .isFalse();

        softAssertions.assertAll();
    }

    @Test
    void isNotCommand() {
        var softAssertions = new SoftAssertions();

        Stream.of(SEPARATOR_TOKEN, NUMBER_TOKEN, TEXT_TOKEN, FLAG_TOKEN)
                .forEach(token -> softAssertions.assertThat(Token.isNotCommand(token))
                        .withFailMessage(() -> "expected not command, got: " + token)
                        .isTrue()
                );

        softAssertions.assertThat(Token.isNotCommand(COMMAND_TOKEN))
                .withFailMessage("expected command, got: " + COMMAND_TOKEN)
                .isFalse();

        softAssertions.assertAll();
    }

    private void assertThatTokenHasValueAndType(Token token, String value, TokenType number) {
        assertThat(token)
                .hasFieldOrPropertyWithValue("type", number)
                .hasFieldOrPropertyWithValue("value", value);
    }

}