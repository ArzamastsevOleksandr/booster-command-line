package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ListNotesCommandArgs;
import cliclient.dto.NoteCollection;
import cliclient.dto.NoteDto;
import cliclient.feign.NotesServiceClient;
import cliclient.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListNotesCommandHandler implements CommandHandler {

    private final NotesServiceClient notesServiceClient;
    private final NoteService noteService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ListNotesCommandArgs) commandArgs;
        args.id().ifPresentOrElse(this::displayNoteById, this::displayAllNotes);
    }

    private void displayNoteById(Long id) {
        adapter.writeLine(notesServiceClient.findById(id).note());
    }

    private void displayAllNotes() {
//        List<Note> notes = noteService.findAll();
        NoteCollection noteCollection = notesServiceClient.getAll();
        Collection<NoteDto> notes = noteCollection.notes();
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
