package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.model.Settings;
import com.booster.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSettingsCommandHandler implements CommandHandler {

    private final SettingsService settingsService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        Settings settings = settingsService.add(commandWithArgs.getLanguageId().orElse(null));
        adapter.writeLine(settings);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_SETTINGS;
    }

}
