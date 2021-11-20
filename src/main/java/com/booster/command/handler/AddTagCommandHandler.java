package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Tag;
import com.booster.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagCommandHandler implements CommandHandler {

    private final TagService tagService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName().ifPresent(name -> {
            Tag tag = tagService.add(name);
            adapter.writeLine(tag);
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
