package cliclient.command.args;

import cliclient.command.Command;

public record AddTagCmdArgs(String name) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
