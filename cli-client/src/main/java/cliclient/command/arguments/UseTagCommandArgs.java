package cliclient.command.arguments;

import java.util.Optional;

public record UseTagCommandArgs(String tag,
                                Optional<Long> noteId) implements CommandArgs {
}
