package booster.command.arguments;

import java.util.Optional;

public record ListNotesCommandArgs(Optional<Long> id) implements CommandArgs {
}
