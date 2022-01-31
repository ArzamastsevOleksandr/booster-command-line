package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.DeleteLanguageCommandArgs;
import cliclient.feign.vocabulary.LanguageControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final LanguageControllerApiClient languageControllerApiClient;

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
