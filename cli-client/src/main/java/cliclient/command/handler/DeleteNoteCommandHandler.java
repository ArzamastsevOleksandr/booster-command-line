package cliclient.command.handler;

import api.notes.NoteApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.DeleteNoteCommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NoteApi noteApi;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteNoteCommandArgs) commandArgs;
        noteApi.deleteById(args.id());
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
