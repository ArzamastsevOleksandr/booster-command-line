package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.AddSettingsArgs;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.dao.SettingsDao;
import com.booster.dao.params.AddSettingsDaoParams;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddSettingsCommandHandler implements CommandHandler {

    // todo: use dao in the service layer only?
    private final SettingsDao settingsDao;

    private final CommandLineWriter commandLineWriter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (AddSettingsArgs) commandWithArguments.getArgs();
            var params = AddSettingsDaoParams.builder()
                    .languageId(args.getLanguageId())
                    .build();

            settingsDao.add(params);
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
        return Command.ADD_SETTINGS;
    }

}
