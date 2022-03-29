package cliclient.command.handler;

import cliclient.command.args.CmdArgs;

public interface CommandHandler {

    void handle(CmdArgs cwa);

    Class<? extends CmdArgs> getCmdArgsClass();

}
