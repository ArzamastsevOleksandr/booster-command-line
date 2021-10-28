package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.SettingsDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final SettingsDao settingsDao;

    private final CommandLineWriter commandLineWriter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            settingsDao.deleteAll();
            commandLineWriter.writeLine("Done.");
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
        commandLineWriter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_SETTINGS;
    }

}
