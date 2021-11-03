package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.NoteDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteNoteCommandHandler implements CommandHandler {

    private final NoteDao noteDao;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(noteDao::delete);
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_NOTE;
    }

}
