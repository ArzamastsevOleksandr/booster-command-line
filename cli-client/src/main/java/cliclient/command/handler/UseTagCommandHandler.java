package cliclient.command.handler;

import api.notes.AddTagsToNoteInput;
import api.notes.NoteDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
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

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UseTagCommandArgs) commandArgs;
        NoteDto noteDto = addTagsToNote(args, args.noteId());
        adapter.writeLine(noteDto);
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
