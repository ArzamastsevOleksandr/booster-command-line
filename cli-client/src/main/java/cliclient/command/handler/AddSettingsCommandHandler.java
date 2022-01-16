package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddSettingsCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.model.Settings;
import cliclient.service.SettingsService;
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
