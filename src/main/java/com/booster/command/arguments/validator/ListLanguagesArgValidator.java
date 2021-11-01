package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.LIST_LANGUAGES;

@Component
public class ListLanguagesArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return LIST_LANGUAGES;
    }

}
