package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;

public interface CommandHandler {

    void handle(CommandWithArgs commandWithArgs);

    Command getCommand();

}
