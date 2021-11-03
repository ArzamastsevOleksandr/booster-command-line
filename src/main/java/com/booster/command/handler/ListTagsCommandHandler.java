package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.TagDao;
import com.booster.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListTagsCommandHandler implements CommandHandler {

    private final TagDao tagDao;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        List<Tag> tags = tagDao.findAll();

        if (tags.isEmpty()) {
            adapter.writeLine("There are no tags in the system now.");
        } else {
            adapter.writeLine("All tags:");
            adapter.newLine();

            for (var tag : tags) {
                adapter.writeLine(tag.toString());
            }
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_TAGS;
    }

}
