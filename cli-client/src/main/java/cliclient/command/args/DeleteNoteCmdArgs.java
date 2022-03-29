package cliclient.command.args;

import cliclient.command.Command;

public record DeleteNoteCmdArgs(Long id) implements CmdArgs {

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
