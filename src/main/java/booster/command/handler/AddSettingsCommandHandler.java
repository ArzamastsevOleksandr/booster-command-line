package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.AddSettingsCommandArgs;
import booster.command.arguments.CommandArgs;
import booster.model.Settings;
import booster.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSettingsCommandHandler implements CommandHandler {

    private final SettingsService settingsService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddSettingsCommandArgs) commandArgs;
        Settings settings = settingsService.add(args.languageId());
        adapter.writeLine(settings);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_SETTINGS;
    }

}
