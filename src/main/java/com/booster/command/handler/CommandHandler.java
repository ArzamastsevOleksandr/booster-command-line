package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;

import java.util.List;

public interface CommandHandler {

    default boolean noErrors(CommandWithArguments commandWithArguments) {
        List<String> argErrors = commandWithArguments.getArgErrors();
        return argErrors == null || argErrors.isEmpty();
    }

}
