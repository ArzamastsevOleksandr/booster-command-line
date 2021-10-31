package com.booster.command.arguments;

import com.booster.command.Command;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@Builder
public class CommandWithArguments {

    Command command;

    Long id;
    String name;
    String definition;

    @Builder.Default
    List<String> argErrors = List.of();

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

    public static CommandWithArguments withErrors(List<String> errors) {
        return CommandWithArguments.builder()
                .argErrors(errors)
                .build();
    }

    @Deprecated
    // todo: forbid null values
    Args args;

    public boolean hasNoErrors() {
        return argErrors == null || argErrors.isEmpty();
    }

    public boolean hasErrors() {
        return !hasNoErrors();
    }

}
