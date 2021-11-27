package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;

public interface CommandHandler {

    void handle(CommandArgs commandArgs);

    Command getCommand();

}
