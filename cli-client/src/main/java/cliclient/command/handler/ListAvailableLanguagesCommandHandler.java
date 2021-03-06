package cliclient.command.handler;

import api.vocabulary.LanguageApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.ListAvailableLanguagesCmdWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAvailableLanguagesCommandHandler implements CommandHandler {

    private final LanguageApi languageApi;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CmdArgs cwa) {
        List<String> languageNames = languageApi.availableLanguages();
        if (languageNames.isEmpty()) {
            adapter.error("No records");
        } else {
            languageNames.forEach(adapter::writeLine);
        }
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return ListAvailableLanguagesCmdWithArgs.class;
    }

}
