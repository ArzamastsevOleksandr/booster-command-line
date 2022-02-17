package cliclient.command.arguments;

import cliclient.command.Command;

import java.util.Optional;

public record HelpCommandArgs(Command helpTarget) implements CommandArgs {

    public Optional<Command> getHelpTarget() {
        return Optional.ofNullable(helpTarget);
    }

}
