package cliclient.command.args;

import cliclient.command.Command;

public record AddNoteCmdArgs(String content, String tag) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.ADD_NOTE;
    }

}
