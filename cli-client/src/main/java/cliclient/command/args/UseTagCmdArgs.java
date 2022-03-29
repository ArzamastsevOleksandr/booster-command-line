package cliclient.command.args;

import cliclient.command.Command;

public record UseTagCmdArgs(Long noteId, String tag) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
