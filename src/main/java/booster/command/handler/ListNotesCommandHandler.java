package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.ListNotesCommandArgs;
import booster.model.Note;
import booster.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListNotesCommandHandler implements CommandHandler {

    private final NoteService noteService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ListNotesCommandArgs) commandArgs;
        args.id().ifPresentOrElse(this::displayNoteById, this::displayAllNotes);
    }

    private void displayNoteById(Long id) {
        adapter.writeLine(noteService.findById(id));
    }

    private void displayAllNotes() {
        List<Note> notes = noteService.findAll();

        if (notes.isEmpty()) {
            adapter.writeLine("There are no notes in the system now.");
        } else {
            adapter.writeLine("All notes:");
            adapter.newLine();

            notes.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_NOTES;
    }

}
