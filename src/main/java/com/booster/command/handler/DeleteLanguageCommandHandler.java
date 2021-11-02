package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.LanguageDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageCommandHandler implements CommandHandler {

    private final LanguageDao languageDao;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(languageDao::delete);
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_LANGUAGE;
    }

}
