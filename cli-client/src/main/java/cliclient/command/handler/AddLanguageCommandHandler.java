package cliclient.command.handler;

import api.vocabulary.AddLanguageInput;
import api.vocabulary.LanguageApi;
import api.vocabulary.LanguageDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddLanguageCommandArgs;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageCommandHandler implements CommandHandler {

    private final LanguageApi languageControllerApi;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddLanguageCommandArgs) commandArgs;
        LanguageDto languageDto = languageControllerApi.add(new AddLanguageInput(args.name()));
        adapter.writeLine(languageDto);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE;
    }

}
