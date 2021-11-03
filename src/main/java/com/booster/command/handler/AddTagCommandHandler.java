package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.TagDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagCommandHandler implements CommandHandler {

    private final TagDao tagDao;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName()
                .ifPresent(tagDao::add);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
