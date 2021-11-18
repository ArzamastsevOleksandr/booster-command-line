package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.params.AddNoteDaoParams;
import com.booster.model.Note;
import com.booster.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AddNoteCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final NoteService noteService;

    // todo: data must be present here after the validation is complete
    // todo: introduce new data types that have the minimum amount of optional data
    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getContent().ifPresent(content -> {
            Set<String> tags = commandWithArgs.getTag().map(Set::of).orElse(Set.of());
            Note note = noteService.add(AddNoteDaoParams.builder().content(content).tags(tags).build());
            adapter.writeLine(note);
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_NOTE;
    }

}
