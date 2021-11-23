package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;

public interface CommandHandler {

    void handle(CommandWithArgs commandWithArgs);

    Command getCommand();

}
