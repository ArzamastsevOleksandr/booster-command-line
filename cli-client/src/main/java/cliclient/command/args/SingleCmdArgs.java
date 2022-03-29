package cliclient.command.args;

import cliclient.command.Command;

public record SingleCmdArgs(Command command) implements CmdArgs {

    @Override
    public Command getCommand() {
        return command;
    }

}
