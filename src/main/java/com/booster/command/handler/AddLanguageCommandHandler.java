package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageCommandHandler implements CommandHandler {

    private final LanguageDao languageDao;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName().ifPresent(name -> {
            long id = languageDao.add(name);
            adapter.writeLine(languageDao.findById(id));
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE;
    }

}
