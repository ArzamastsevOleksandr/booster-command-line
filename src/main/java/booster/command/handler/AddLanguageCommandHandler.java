package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.AddLanguageCommandArgs;
import booster.command.arguments.CommandArgs;
import booster.model.Language;
import booster.service.LanguageService;
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
