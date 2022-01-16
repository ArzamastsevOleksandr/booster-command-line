package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddNoteCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.dao.params.AddNoteDaoParams;
import cliclient.model.Note;
import cliclient.service.NoteService;
import cliclient.service.SessionTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NoteService noteService;
    private final SessionTrackerService sessionTrackerService;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddNoteCommandArgs) commandArgs;
        Note note = noteService.add(AddNoteDaoParams.builder().content(args.content()).tags(args.tags()).build());
        adapter.writeLine(note);
        adapter.writeLine("Notes added so far: " + sessionTrackerService.getNotesAddedCount());
    }

    @Override
    public Command getCommand() {
        return Command.ADD_NOTE;
    }

}
