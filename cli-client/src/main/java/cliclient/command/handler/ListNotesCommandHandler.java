package cliclient.command.handler;

import api.notes.NoteDto;
import api.notes.PatchNoteLastSeenAtInput;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ListNotesCommandArgs;
import cliclient.feign.notes.NotesServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListNotesCommandHandler implements CommandHandler {

    private final NotesServiceClient notesServiceClient;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ListNotesCommandArgs) commandArgs;
        args.id().ifPresentOrElse(this::displayNoteById, () -> displayNotes(args));
    }

    private void displayNoteById(Long id) {
        NoteDto noteDto = notesServiceClient.getById(id);
        showAndUpdateLastSeenAt(noteDto);
    }

    private void showAndUpdateLastSeenAt(NoteDto noteDto) {
        adapter.writeLine(noteDto);
        updateLastSeenAt(List.of(noteDto.getId()));
    }

    private void displayNotes(ListNotesCommandArgs args) {
        var paginator = new Paginator<NoteDto>(args.pagination(), notesServiceClient.countAll()) {
            @Override
            List<NoteDto> nextBatch() {
                return notesServiceClient.findFirst(limit());
            }
        };
        display(paginator);
    }

    // todo: DRY
    private void display(Paginator<NoteDto> paginator) {
        if (paginator.isEmpty()) {
            adapter.error("No records");
        } else {
            displayAndUpdateLastSeenAt(paginator);

            String line = readLineIfInRangeOrEnd(paginator);
            while (!line.equals("e") && paginator.isInRange()) {
                displayAndUpdateLastSeenAt(paginator);
                line = readLineIfInRangeOrEnd(paginator);
            }
        }
    }

    private void displayAndUpdateLastSeenAt(Paginator<NoteDto> paginator) {
        adapter.writeLine(paginator.counter());
        // todo: color
        List<NoteDto> noteDtos = paginator.nextBatchAndUpdateRange();
        noteDtos.forEach(adapter::writeLine);
        List<Long> noteIds = noteDtos
                .stream()
                .map(NoteDto::getId)
                .toList();
        updateLastSeenAt(noteIds);
    }

    private void updateLastSeenAt(List<Long> noteIds) {
        notesServiceClient.patchLastSeenAt(PatchNoteLastSeenAtInput.builder()
                .ids(noteIds)
                .lastSeenAt(new Timestamp(System.currentTimeMillis()))
                .build());
    }

    private String readLineIfInRangeOrEnd(Paginator<NoteDto> paginator) {
        return paginator.isInRange() ? adapter.readLine() : "e";
    }

    @Override
    public Command getCommand() {
        return Command.LIST_NOTES;
    }

}
