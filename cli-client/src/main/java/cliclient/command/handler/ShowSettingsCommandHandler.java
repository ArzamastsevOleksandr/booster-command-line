package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowSettingsCommandHandler implements CommandHandler {

    private final SettingsService settingsService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        settingsService.findOne().ifPresentOrElse(
                adapter::writeLine,
                () -> adapter.writeLine("There are no settings in the system now.")
        );
    }

    @Override
    public Command getCommand() {
        return Command.SHOW_SETTINGS;
    }

}
