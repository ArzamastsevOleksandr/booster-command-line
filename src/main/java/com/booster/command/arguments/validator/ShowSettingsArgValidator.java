package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.SHOW_SETTINGS;

@Component
@RequiredArgsConstructor
public class ShowSettingsArgValidator implements ArgValidator {

    @Override
    public CommandWithArguments validateAndReturn(CommandWithArguments commandWithArguments) {
        return commandWithArguments;
    }

    @Override
    public Command command() {
        return SHOW_SETTINGS;
    }

}
