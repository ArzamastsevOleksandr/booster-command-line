package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.service.SettingsService;
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
