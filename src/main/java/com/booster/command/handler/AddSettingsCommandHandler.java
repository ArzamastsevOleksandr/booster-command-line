package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.SettingsDao;
import com.booster.dao.params.AddSettingsDaoParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSettingsCommandHandler implements CommandHandler {

    // todo: use dao in the service layer only?
    private final SettingsDao settingsDao;

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.hasNoErrors()) {
            AddSettingsDaoParams params = commandWithArgs.getLanguageId()
                    .map(AddSettingsDaoParams::of)
                    .orElseGet(AddSettingsDaoParams::empty);

            settingsDao.add(params);
            adapter.writeLine("Done.");
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArgs.getErrors()
                    .forEach(adapter::writeLine);
        }
        adapter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.ADD_SETTINGS;
    }

}
