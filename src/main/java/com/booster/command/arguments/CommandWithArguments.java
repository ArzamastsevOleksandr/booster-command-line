package com.booster.command.arguments;

import com.booster.command.Command;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommandWithArguments {

    private Command command;

    @Deprecated
    @Builder.Default
    private List<String> arguments = List.of();

    private Args args;

    @Builder.Default
    private List<String> argErrors = List.of();

    public boolean hasNoErrors() {
        return argErrors == null || argErrors.isEmpty();
    }

}
