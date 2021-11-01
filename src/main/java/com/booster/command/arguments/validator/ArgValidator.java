package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;

public interface ArgValidator {

    Runnable ID_IS_MISSING = () -> {
        throw new ArgsValidationException("Id is missing");
    };

    default CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        try {
            return validateAndReturn(commandWithArguments);
        } catch (ArgsValidationException e) {
            return getCommandWithArgumentsBuilder().argErrors(e.errors).build();
        }
    }

    CommandWithArguments validateAndReturn(CommandWithArguments commandWithArguments);

    Command command();

    //    todo: rename
    default CommandWithArguments.CommandWithArgumentsBuilder getCommandWithArgumentsBuilder() {
        return CommandWithArguments.builder()
                .command(command());
    }

}
