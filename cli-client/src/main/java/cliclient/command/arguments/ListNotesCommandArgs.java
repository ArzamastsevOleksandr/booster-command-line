package cliclient.command.arguments;

import java.util.Optional;

public record ListNotesCommandArgs(Optional<Long> id, Integer pagination) implements CommandArgs {
}
