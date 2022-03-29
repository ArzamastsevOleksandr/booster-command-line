package cliclient.command.args;

import cliclient.command.Command;

import java.util.Optional;

public record ListNotesCmdArgs(Long id, Integer pagination) implements CmdArgs {

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    @Override
    public Command getCommand() {
        return Command.LIST_NOTES;
    }

}
