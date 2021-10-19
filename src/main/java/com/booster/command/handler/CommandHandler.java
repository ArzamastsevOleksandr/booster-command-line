package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;

public interface CommandHandler {

    void handle(CommandWithArguments commandWithArguments);

    Command getCommand();

}
