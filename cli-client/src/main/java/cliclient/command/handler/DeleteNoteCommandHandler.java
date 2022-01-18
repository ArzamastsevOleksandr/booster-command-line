package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.DeleteNoteCommandArgs;
import cliclient.feign.notes.NotesServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteNoteCommandHandler implements CommandHandler {

    private final NotesServiceClient notesServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteNoteCommandArgs) commandArgs;
        notesServiceClient.delete(args.id());
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
