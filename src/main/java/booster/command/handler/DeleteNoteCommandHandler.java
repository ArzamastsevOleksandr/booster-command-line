package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.DeleteNoteCommandArgs;
import booster.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteNoteCommandHandler implements CommandHandler {

    private final NoteService noteService;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteNoteCommandArgs) commandArgs;
        noteService.delete(args.id());
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
