package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.dao.SettingsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final SettingsDao settingsDao;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        settingsDao.deleteAll();
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_SETTINGS;
    }

}
