package cliclient.command.handler;

import api.notes.NoteApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.DeleteNoteCmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NoteApi noteApi;

    @Override
    public void handle(CmdArgs cmdArgs) {
        var args = (DeleteNoteCmdArgs) cmdArgs;
        noteApi.deleteById(args.id());
        adapter.writeLine("Done");
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return DeleteNoteCmdArgs.class;
    }

}
