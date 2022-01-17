package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddNoteCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.dao.params.AddCause;
import cliclient.feign.AddNoteInput;
import cliclient.feign.NoteResponse;
import cliclient.feign.NotesServiceClient;
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
        var input = new AddNoteInput(args.content());
        NoteResponse noteResponse = notesServiceClient.add(input);
        sessionTrackerService.incNotesCount(AddCause.CREATE);
        adapter.writeLine(noteResponse.note());
        adapter.writeLine("Notes added so far: " + sessionTrackerService.getNotesAddedCount());
    }

    @Override
    public Command getCommand() {
        return Command.ADD_NOTE;
    }

}
