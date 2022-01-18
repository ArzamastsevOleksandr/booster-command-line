package cliclient.command.handler;

import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddLanguageCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.vocabulary.LanguageControllerApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageCommandHandler implements CommandHandler {

    private final LanguageControllerApiClient languageControllerApiClient;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddLanguageCommandArgs) commandArgs;
        LanguageDto languageDto = languageControllerApiClient.add(new AddLanguageInput(args.name()));
        adapter.writeLine(languageDto);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE;
    }

}
