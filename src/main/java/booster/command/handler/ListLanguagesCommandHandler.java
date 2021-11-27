package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.model.Language;
import booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler implements CommandHandler {

    private final LanguageService languageService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
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
