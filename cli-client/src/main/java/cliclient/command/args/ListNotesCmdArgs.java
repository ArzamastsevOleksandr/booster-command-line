package cliclient.command.args;

import java.util.Optional;

public record ListNotesCmdArgs(Long id, Integer pagination) implements CmdArgs {

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

}
