package cliclient.command.arguments;

public record UseTagCommandArgs(String tag,
                                Long noteId) implements CommandArgs {
}
