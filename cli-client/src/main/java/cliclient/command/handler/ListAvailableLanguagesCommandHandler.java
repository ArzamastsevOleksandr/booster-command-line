package cliclient.command.handler;

import api.vocabulary.LanguageApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAvailableLanguagesCommandHandler implements CommandHandler {

    private final LanguageApi languageApi;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        List<String> languageNames = languageApi.availableLanguages();
        if (languageNames.isEmpty()) {
            adapter.error("No records");
        } else {
            languageNames.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_AVAILABLE_LANGUAGES;
    }

}
