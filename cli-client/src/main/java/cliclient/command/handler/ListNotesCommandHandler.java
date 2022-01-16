package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ListNotesCommandArgs;
import cliclient.model.Note;
import cliclient.service.NoteService;
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
