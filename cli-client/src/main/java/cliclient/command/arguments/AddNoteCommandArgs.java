package cliclient.command.arguments;

import java.util.Set;

public record AddNoteCommandArgs(String content, Set<String> tags) implements CommandArgs {
}
