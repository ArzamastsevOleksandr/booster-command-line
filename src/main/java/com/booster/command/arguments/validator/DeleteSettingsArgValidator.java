package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.DELETE_SETTINGS;

@Component
public class DeleteSettingsArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return DELETE_SETTINGS;
    }

}
