package cliclient.command.handler;

import api.settings.SettingsDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.settings.SettingsServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsServiceClient settingsServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        SettingsDto settingsDto = settingsServiceClient.findOne();
        adapter.writeLine(settingsDto);
    }

    @Override
    public Command getCommand() {
        return Command.SHOW_SETTINGS;
    }

}
