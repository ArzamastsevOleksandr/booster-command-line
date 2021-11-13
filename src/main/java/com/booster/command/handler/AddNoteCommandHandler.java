package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.NoteDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddNoteCommandHandler implements CommandHandler {

    private final NoteDao noteDao;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getContent().ifPresent(content -> {
            long id = noteDao.add(content);
            adapter.writeLine(noteDao.findById(id));
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_NOTE;
    }

}
