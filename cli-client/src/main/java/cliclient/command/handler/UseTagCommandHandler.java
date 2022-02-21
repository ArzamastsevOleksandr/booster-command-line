package cliclient.command.handler;

import api.notes.AddTagsToNoteInput;
import api.notes.NoteDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.HelpCommandArgs;
import cliclient.command.arguments.UseTagCommandArgs;
import cliclient.feign.notes.NotesServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UseTagCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NotesServiceClient notesServiceClient;
    private final HelpCommandHandler helpCommandHandler;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UseTagCommandArgs) commandArgs;
        args.noteId()
                .map(id -> addTagsToNote(args, id))
                .ifPresentOrElse(adapter::writeLine, () -> {
                    adapter.error("No target for tag usage specified");
                    adapter.newLine();
                    helpCommandHandler.handle(new HelpCommandArgs(Command.USE_TAG));
                });
    }

    private NoteDto addTagsToNote(UseTagCommandArgs args, Long id) {
        String tag = args.tag();

        return notesServiceClient.addTags(AddTagsToNoteInput.builder()
                .noteId(id)
                .tagNames(Set.of(tag))
                .build());
    }

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
