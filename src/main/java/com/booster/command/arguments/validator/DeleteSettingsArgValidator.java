package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.DELETE_SETTINGS;

@Component
public class DeleteSettingsArgValidator implements ArgValidator {

    @Override
    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        return commandWithArguments;
    }

    @Override
    public Command command() {
        return DELETE_SETTINGS;
    }

}
