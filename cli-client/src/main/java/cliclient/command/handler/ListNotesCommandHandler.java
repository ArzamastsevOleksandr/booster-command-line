package cliclient.command.handler;

import api.notes.NoteApi;
import api.notes.NoteDto;
import api.notes.PatchNoteLastSeenAtInput;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.ListNotesCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ListNotesCommandHandler implements CommandHandler {

    private final NoteApi noteApi;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CmdArgs cwa) {
        var args = (ListNotesCmdArgs) cwa;
        args.getId().ifPresentOrElse(this::displayNoteById, () -> displayNotes(args));
    }

    private void displayNoteById(Long id) {
        NoteDto noteDto = noteApi.getById(id);
        showAndUpdateLastSeenAt(noteDto);
    }

    private void showAndUpdateLastSeenAt(NoteDto noteDto) {
        adapter.writeLine(noteDto);
        updateLastSeenAt(List.of(noteDto.getId()));
    }

    private void displayNotes(ListNotesCmdArgs args) {
        var paginator = new Paginator<NoteDto>(args.pagination(), noteApi.countAll()) {
            @Override
            List<NoteDto> nextBatch() {
                return noteApi.findFirstWithSmallestLastSeenAt(limit());
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
        List<NoteDto> noteDtos = paginator.nextBatchAndUpdateRange();
        noteDtos.forEach(adapter::writeLine);
        List<Long> noteIds = noteDtos
                .stream()
                .map(NoteDto::getId)
                .toList();
        updateLastSeenAt(noteIds);
    }

    private void updateLastSeenAt(List<Long> noteIds) {
        noteApi.patchLastSeenAt(PatchNoteLastSeenAtInput.builder()
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
