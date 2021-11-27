package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final SettingsService settingsService;

    @Override
    public void handle(CommandArgs commandArgs) {
        settingsService.deleteAll();
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_SETTINGS;
    }

}
