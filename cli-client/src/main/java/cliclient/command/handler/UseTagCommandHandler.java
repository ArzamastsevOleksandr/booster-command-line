package cliclient.command.handler;

import api.notes.AddTagsToNoteInput;
import api.notes.NoteApi;
import api.notes.NoteDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.UseTagCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UseTagCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NoteApi noteApi;

    @Override
    public void handle(CmdArgs cwa) {
        var args = (UseTagCmdArgs) cwa;
        NoteDto noteDto = noteApi.addTags(AddTagsToNoteInput.builder()
                .noteId(args.noteId())
                .tagNames(Set.of(args.tag()))
                .build());
        adapter.writeLine(noteDto);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return UseTagCmdArgs.class;
    }

}
