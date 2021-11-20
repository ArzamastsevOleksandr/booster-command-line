package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Tag;
import com.booster.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListTagsCommandHandler implements CommandHandler {

    private final TagService tagService;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        List<Tag> tags = tagService.findAll();

        if (tags.isEmpty()) {
            adapter.writeLine("There are no tags in the system now.");
        } else {
            adapter.writeLine("All tags:");
            adapter.newLine();

            tags.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_TAGS;
    }

}
