package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.service.SettingsService;
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
