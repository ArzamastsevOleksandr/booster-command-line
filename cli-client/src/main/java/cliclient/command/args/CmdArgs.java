package cliclient.command.args;

import cliclient.command.Command;

public interface CmdArgs {

    @Deprecated
    default Command getCommand() {
        return null;
    }

    default boolean isNotExit() {
        return true;
    }

    default boolean isNoInput() {
        return false;
    }

    default boolean hasNoErrors() {
        return true;
    }

}
