package cliclient.parser;

import cliclient.command.Command;
import cliclient.command.FlagType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class TokenizerTest {

    Tokenizer tokenizer = new Tokenizer();

    @Test
    void returnsEmptyTokensForEmptyInput() {
        List<Token> tokens = tokenizer.parseIntoTokens("");

        assertThat(tokens).hasSize(0);
    }

    @Test
    void stripsWhitespacesAndParsesSingleRecognizableCommands() {
        Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .map(Command::getEquivalents)
                .flatMap(Set::stream)
                .map(this::padWithSpaces)
                .forEach(this::assertThatRecognizableCommandWrappedWithSpacesIsParsed);
    }

    private String padWithSpaces(Object s) {
        return "   " + s + "   ";
    }

    private void assertThatRecognizableCommandWrappedWithSpacesIsParsed(String cmdEquivalent) {
        List<Token> tokens = tokenizer.parseIntoTokens(cmdEquivalent);

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0))
                .hasFieldOrPropertyWithValue("type", TokenType.COMMAND)
                .hasFieldOrPropertyWithValue("value", cmdEquivalent.strip());
    }

    @Test
    void stripsWhitespacesAndParsesSingleKnownFlags() {
        Arrays.stream(FlagType.values())
                .filter(FlagType::isKnown)
                .map(f -> f.value)
                .map(this::prefixWithFlagMarker)
                .map(this::padWithSpaces)
                .forEach(this::assertThatKnownFlagWithFlagMarkerWrappedWithSpacesIsParsed);
    }

    @Test
    void stripsWhitespacesAndParsesPositiveLongNumbers() {
        // todo: add negative numbers parsing
        LongStream.iterate(1, i -> i <= 1001, i -> i + 10)
                .mapToObj(this::padWithSpaces)
                .forEach(this::assertThatPositiveLongNumberWrappedWithSpacesIsParsed);
    }

    @Test
    void stripsWhitespacesAndParsesSeparators() {
        List<Token> tokens = tokenizer.parseIntoTokens(" =  =   = ");

        assertThat(tokens).hasSize(3);
        tokens.forEach(token -> assertThat(token)
                .hasFieldOrPropertyWithValue("type", TokenType.SEPARATOR)
                .hasFieldOrPropertyWithValue("value", Token.SEPARATOR)
        );
    }

    @Test
    void stripsWhitespacesAndParsesText() {
        Arrays.stream(Command.values())
                .map(Command::getEquivalents)
                .flatMap(Set::stream)
                .map(c -> c + "a")
                .map(this::padWithSpaces)
                .forEach(this::assertThatTextWrappedWithSpacesIsParsed);
    }

    @Test
    void parsesInputStringIntoCorrectTokenSequence() {
        String input = "  ave \\id  =1 \\n = one \\syn=one1;one2 \\ant=one1;one2 \\def=very long description ";
        List<Token> tokens = tokenizer.parseIntoTokens(input);

        assertThat(tokens).hasSize(16);

        assertThat(tokens.get(0))
                .hasFieldOrPropertyWithValue("type", TokenType.COMMAND)
                .hasFieldOrPropertyWithValue("value", "ave");

        assertThat(tokens.get(1))
                .hasFieldOrPropertyWithValue("type", TokenType.FLAG)
                .hasFieldOrPropertyWithValue("value", "id");

        assertThat(tokens.get(2))
                .hasFieldOrPropertyWithValue("type", TokenType.SEPARATOR)
                .hasFieldOrPropertyWithValue("value", "=");

        assertThat(tokens.get(3))
                .hasFieldOrPropertyWithValue("type", TokenType.NUMBER)
                .hasFieldOrPropertyWithValue("value", "1");

        assertThat(tokens.get(4))
                .hasFieldOrPropertyWithValue("type", TokenType.FLAG)
                .hasFieldOrPropertyWithValue("value", "n");

        assertThat(tokens.get(5))
                .hasFieldOrPropertyWithValue("type", TokenType.SEPARATOR)
                .hasFieldOrPropertyWithValue("value", "=");

        assertThat(tokens.get(6))
                .hasFieldOrPropertyWithValue("type", TokenType.TEXT)
                .hasFieldOrPropertyWithValue("value", "one");

        assertThat(tokens.get(7))
                .hasFieldOrPropertyWithValue("type", TokenType.FLAG)
                .hasFieldOrPropertyWithValue("value", "syn");

        assertThat(tokens.get(8))
                .hasFieldOrPropertyWithValue("type", TokenType.SEPARATOR)
                .hasFieldOrPropertyWithValue("value", "=");

        assertThat(tokens.get(9))
                .hasFieldOrPropertyWithValue("type", TokenType.TEXT)
                .hasFieldOrPropertyWithValue("value", "one1;one2");

        assertThat(tokens.get(10))
                .hasFieldOrPropertyWithValue("type", TokenType.FLAG)
                .hasFieldOrPropertyWithValue("value", "ant");

        assertThat(tokens.get(11))
                .hasFieldOrPropertyWithValue("type", TokenType.SEPARATOR)
                .hasFieldOrPropertyWithValue("value", "=");

        assertThat(tokens.get(12))
                .hasFieldOrPropertyWithValue("type", TokenType.TEXT)
                .hasFieldOrPropertyWithValue("value", "one1;one2");

        assertThat(tokens.get(13))
                .hasFieldOrPropertyWithValue("type", TokenType.FLAG)
                .hasFieldOrPropertyWithValue("value", "def");

        assertThat(tokens.get(14))
                .hasFieldOrPropertyWithValue("type", TokenType.SEPARATOR)
                .hasFieldOrPropertyWithValue("value", "=");

        assertThat(tokens.get(15))
                .hasFieldOrPropertyWithValue("type", TokenType.TEXT)
                .hasFieldOrPropertyWithValue("value", "very long description");
    }

    private void assertThatTextWrappedWithSpacesIsParsed(String text) {
        List<Token> tokens = tokenizer.parseIntoTokens(text);

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0))
                .hasFieldOrPropertyWithValue("type", TokenType.TEXT)
                .hasFieldOrPropertyWithValue("value", text.strip());
    }

    private void assertThatPositiveLongNumberWrappedWithSpacesIsParsed(String longNumber) {
        List<Token> tokens = tokenizer.parseIntoTokens(longNumber);

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0))
                .hasFieldOrPropertyWithValue("type", TokenType.NUMBER)
                .hasFieldOrPropertyWithValue("value", longNumber.strip());
    }

    private String prefixWithFlagMarker(String rawFlag) {
        return Token.FLAG_MARKER + rawFlag;
    }

    private void assertThatKnownFlagWithFlagMarkerWrappedWithSpacesIsParsed(String flag) {
        List<Token> tokens = tokenizer.parseIntoTokens(flag);

        assertThat(tokens).hasSize(1);
        assertThat(tokens.get(0))
                .hasFieldOrPropertyWithValue("type", TokenType.FLAG)
                .hasFieldOrPropertyWithValue("value", flag.strip().substring(1));
    }

}