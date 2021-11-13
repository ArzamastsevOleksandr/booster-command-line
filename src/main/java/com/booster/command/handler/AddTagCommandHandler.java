package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagCommandHandler implements CommandHandler {

    private final TagDao tagDao;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName().ifPresent(name -> {
            String tag = tagDao.add(name);
            adapter.writeLine(tagDao.findByName(tag));
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
