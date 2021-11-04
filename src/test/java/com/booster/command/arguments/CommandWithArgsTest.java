package com.booster.command.arguments;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CommandWithArgsTest {

    @Test
    void throwsIAEIfErrorsAreNull() {
        assertThatThrownBy(() -> CommandWithArgs.withErrors(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("errors can not be null");
    }

    @Test
    void createsCommandWithArgsWithErrors() {
        List<String> errors = List.of("Error");

        CommandWithArgs commandWithArgs = CommandWithArgs.withErrors(errors);

        assertThat(commandWithArgs)
                .hasFieldOrPropertyWithValue("errors", errors)
                .hasFieldOrPropertyWithValue("command", null);
    }

}