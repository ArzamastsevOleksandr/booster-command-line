package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Language;
import com.booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler implements CommandHandler {

    private final LanguageService languageService;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        List<Language> languages = languageService.findAll();

        if (languages.isEmpty()) {
            adapter.writeLine("There are no languages in the system now.");
        } else {
            adapter.writeLine("All languages:");
            adapter.newLine();

            languages.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_LANGUAGES;
    }

}
