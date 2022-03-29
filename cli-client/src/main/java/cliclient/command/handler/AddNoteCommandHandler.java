package cliclient.command.handler;

import api.notes.AddNoteInput;
import api.notes.NoteApi;
import api.notes.NoteDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.AddNoteCmdArgs;
import cliclient.command.args.CmdArgs;
import cliclient.service.SessionTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SessionTrackerService sessionTrackerService;
    private final NoteApi noteApi;

    @Override
    public void handle(CmdArgs cmdArgs) {
        var args = (AddNoteCmdArgs) cmdArgs;
        NoteDto noteDto = noteApi.add(AddNoteInput.builder()
                .content(args.content())
                .build());
        sessionTrackerService.notesAddedCount++;
        adapter.writeLine(noteDto);
        adapter.writeLine("Notes added so far: " + sessionTrackerService.notesAddedCount);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return AddNoteCmdArgs.class;
    }

}
