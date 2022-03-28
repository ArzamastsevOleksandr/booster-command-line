package cliclient.command.args;

import cliclient.command.Command;

public class NoInputCmdArgs implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.NO_INPUT;
    }

}
