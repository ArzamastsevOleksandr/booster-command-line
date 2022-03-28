package cliclient.command.handler;

import api.notes.NoteApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
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
        noteApi.deleteById(args.getId());
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
