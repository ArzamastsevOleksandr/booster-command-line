package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.AddNoteCommandArgs;
import booster.command.arguments.CommandArgs;
import booster.dao.params.AddNoteDaoParams;
import booster.model.Note;
import booster.service.NoteService;
import booster.service.SessionTrackerService;
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
