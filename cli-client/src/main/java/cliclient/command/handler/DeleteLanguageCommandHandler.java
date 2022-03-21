package cliclient.command.handler;

import api.vocabulary.LanguageApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.DeleteLanguageCommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final LanguageApi languageControllerApiClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteLanguageCommandArgs) commandArgs;
        languageControllerApiClient.deleteById(args.id());
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_LANGUAGE;
    }

}
