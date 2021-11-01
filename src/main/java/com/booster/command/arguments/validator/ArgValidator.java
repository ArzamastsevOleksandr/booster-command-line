package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;

public interface ArgValidator {

    Runnable ID_IS_MISSING = () -> {
        throw new ArgsValidationException("Id is missing");
    };

    default CommandWithArgs validate(CommandWithArgs commandWithArgs) {
        try {
            return validateAndReturn(commandWithArgs);
        } catch (ArgsValidationException e) {
            return getCommandWithArgumentsBuilder().errors(e.errors).build();
        }
    }

    CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs);

    Command command();

    default CommandWithArgs.CommandWithArgsBuilder getCommandWithArgumentsBuilder() {
        return CommandWithArgs.builder()
                .command(command());
    }

}
