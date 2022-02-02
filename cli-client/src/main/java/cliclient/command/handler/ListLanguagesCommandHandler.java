package cliclient.command.handler;

import api.vocabulary.LanguageDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.vocabulary.LanguageControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListLanguagesCommandHandler implements CommandHandler {

    private final LanguageControllerApiClient languageControllerApiClient;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        Collection<LanguageDto> languageDtos = languageControllerApiClient.getAll();
        if (languageDtos.isEmpty()) {
            adapter.error("There are no languages yet");
        } else {
            languageDtos.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_LANGUAGES;
    }

}
