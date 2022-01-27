package cliclient.command.handler;

import api.notes.NoteDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ListNotesCommandArgs;
import cliclient.feign.notes.NotesServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListNotesCommandHandler implements CommandHandler {

    private final NotesServiceClient notesServiceClient;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ListNotesCommandArgs) commandArgs;
        args.id().ifPresentOrElse(this::displayNoteById, this::displayAllNotes);
    }

    private void displayNoteById(Long id) {
        adapter.writeLine(notesServiceClient.getById(id));
    }

    private void displayAllNotes() {
        Collection<NoteDto> notes = notesServiceClient.getAll();
        if (notes.isEmpty()) {
            adapter.writeLine("There are no notes yet");
        } else {
            notes.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_NOTES;
    }

}
