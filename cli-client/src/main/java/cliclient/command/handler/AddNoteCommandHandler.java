package cliclient.command.handler;

import api.notes.AddNoteInput;
import api.notes.NoteDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddNoteCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.notes.NotesServiceClient;
import cliclient.service.SessionTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SessionTrackerService sessionTrackerService;
    private final NotesServiceClient notesServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddNoteCommandArgs) commandArgs;
        NoteDto noteDto = notesServiceClient.add(AddNoteInput.builder()
                .content(args.content())
                .build());
        sessionTrackerService.notesAddedCount++;
        adapter.writeLine(noteDto);
        adapter.writeLine("Notes added so far: " + sessionTrackerService.notesAddedCount);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_NOTE;
    }

}
