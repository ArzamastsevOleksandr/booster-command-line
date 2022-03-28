package cliclient.command.args;

import cliclient.command.Command;

public interface CmdArgs {

    Command getCommand();

    default boolean hasNoErrors() {
        return true;
    }

}
