package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageCommandHandler implements CommandHandler {

    private final LanguageDao languageDao;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName()
                .map(String::toUpperCase)
                .ifPresent(languageDao::add);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE;
    }

}
