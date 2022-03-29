package cliclient.command.args;

import cliclient.command.Command;

public record ErroneousCmdArgs(Command command, String error) implements CmdArgs {

    @Override
    public boolean hasNoErrors() {
        return false;
    }

}
