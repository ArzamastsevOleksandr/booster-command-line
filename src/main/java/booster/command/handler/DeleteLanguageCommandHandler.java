package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.DeleteLanguageCommandArgs;
import booster.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteLanguageCommandHandler implements CommandHandler {

    private final LanguageService languageService;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteLanguageCommandArgs) commandArgs;
        languageService.delete(args.id());
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_LANGUAGE;
    }

}
