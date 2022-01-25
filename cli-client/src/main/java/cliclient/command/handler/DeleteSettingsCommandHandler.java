package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.settings.SettingsServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final SettingsServiceClient settingsServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        settingsServiceClient.delete();
        adapter.writeLine("Done");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_SETTINGS;
    }

}
