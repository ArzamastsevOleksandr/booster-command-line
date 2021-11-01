package com.booster.command.handler;

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

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        AddSettingsDaoParams params = commandWithArgs.getLanguageId()
                .map(AddSettingsDaoParams::of)
                .orElseGet(AddSettingsDaoParams::empty);

        settingsDao.add(params);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_SETTINGS;
    }

}
