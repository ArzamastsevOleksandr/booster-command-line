package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;

public interface CommandHandler {

    void handle(CommandArgs commandArgs);

    Command getCommand();

}
