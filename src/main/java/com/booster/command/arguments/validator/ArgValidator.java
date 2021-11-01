package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;

public interface ArgValidator {

    CommandWithArguments validate(CommandWithArguments commandWithArguments);

    Command command();

    //    todo: rename
    default CommandWithArguments.CommandWithArgumentsBuilder getCommandBuilder() {
        return CommandWithArguments.builder()
                .command(command());
    }

}
