package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddLanguageCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.model.Language;
import cliclient.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddLanguageCommandHandler implements CommandHandler {

    private final LanguageService languageService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddLanguageCommandArgs) commandArgs;
        Language language = languageService.add(args.name());
        adapter.writeLine(language);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_LANGUAGE;
    }

}
