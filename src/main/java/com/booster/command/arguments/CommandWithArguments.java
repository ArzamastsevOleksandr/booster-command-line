package com.booster.command.arguments;

import com.booster.command.Command;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@Builder(toBuilder = true)
public class CommandWithArguments {

    Command command;

    Long id;
    Long languageId;
    String name;
    String definition;
    String filename;
    String mode;

    @Builder.Default
    List<String> argErrors = List.of();

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<Long> getLanguageId() {
        return Optional.ofNullable(languageId);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

    public Optional<String> getFilename() {
        return Optional.ofNullable(filename);
    }

    public Optional<String> getMode() {
        return Optional.ofNullable(mode);
    }

    public static CommandWithArguments withErrors(List<String> errors) {
        return CommandWithArguments.builder()
                .argErrors(errors)
                .build();
    }

    public boolean hasNoErrors() {
        return argErrors == null || argErrors.isEmpty();
    }

}
