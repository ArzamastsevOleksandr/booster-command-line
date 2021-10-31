package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.LIST_LANGUAGES;

@Component
public class ListLanguagesArgValidator implements ArgValidator {

    @Override
    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        return commandWithArguments;
    }

    @Override
    public Command command() {
        return LIST_LANGUAGES;
    }

}
