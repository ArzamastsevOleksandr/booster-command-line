package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageCommandHandler implements CommandHandler {

    private final LanguageService languageService;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getId().ifPresent(languageService::delete);
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_LANGUAGE;
    }

}
