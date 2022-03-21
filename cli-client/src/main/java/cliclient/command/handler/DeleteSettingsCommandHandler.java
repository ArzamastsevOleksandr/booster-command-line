package cliclient.command.handler;

import api.settings.SettingsApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsApi settingsApi;

    @Override
    public void handle(CommandArgs commandArgs) {
        settingsApi.delete();
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_SETTINGS;
    }

}
