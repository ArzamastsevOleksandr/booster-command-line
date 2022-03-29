package cliclient.command.args;

import cliclient.command.Command;

import java.util.Optional;

public record HelpCmdArgs(Command helpTarget) implements CmdArgs {

    public Optional<Command> getHelpTarget() {
        return Optional.ofNullable(helpTarget);
    }

    public Command getCommand() {
        return Command.HELP;
    }

}
