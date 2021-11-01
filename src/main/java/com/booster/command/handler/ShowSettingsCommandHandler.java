package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowSettingsCommandHandler implements CommandHandler {

    private final SettingsService settingsService;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        settingsService.findOne().ifPresentOrElse(
                settings -> adapter.writeLine(settings.toString()),
                () -> adapter.writeLine("There are no settings in the system now.")
        );
    }

    @Override
    public Command getCommand() {
        return Command.SHOW_SETTINGS;
    }

}
