package cliclient.command.args;

public interface CmdArgs {

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
