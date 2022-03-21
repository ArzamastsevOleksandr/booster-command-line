package cliclient.command.handler;

import api.vocabulary.LanguageApi;
import api.vocabulary.LanguageDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler implements CommandHandler {

    private final LanguageApi languageControllerApiClient;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        Collection<LanguageDto> languageDtos = languageControllerApiClient.getAll();
        if (languageDtos.isEmpty()) {
            adapter.error("No records");
        } else {
            languageDtos.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_LANGUAGES;
    }

}
