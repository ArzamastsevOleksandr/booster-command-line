package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.args.CmdArgs;

public interface CommandHandler {

    void handle(CmdArgs cwa);

    Command getCommand();

}
