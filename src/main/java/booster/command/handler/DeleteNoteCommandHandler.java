package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import booster.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteNoteCommandHandler implements CommandHandler {

    private final NoteService noteService;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(noteService::delete);
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
