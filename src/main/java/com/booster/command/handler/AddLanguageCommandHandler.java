package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Language;
import com.booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageCommandHandler implements CommandHandler {

    private final LanguageService languageService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName().ifPresent(name -> {
            Language language = languageService.add(name);
            adapter.writeLine(language);
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE;
    }

}
