package com.booster.command.arguments;

import com.booster.command.Command;
import lombok.Builder;
import lombok.Data;

import java.util.List;

// todo: VO?
@Data
@Builder
public class CommandWithArguments {

    private Command command;

    // todo: forbid null values
    private Args args;

    @Builder.Default
    private List<String> argErrors = List.of();

    public boolean hasNoErrors() {
        return argErrors == null || argErrors.isEmpty();
    }

}
