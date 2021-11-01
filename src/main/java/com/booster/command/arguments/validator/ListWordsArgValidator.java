package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.LIST_WORDS;

@Component
public class ListWordsArgValidator implements ArgValidator {

    @Override
    public CommandWithArguments validateAndReturn(CommandWithArguments commandWithArguments) {
        return commandWithArguments;
    }

    @Override
    public Command command() {
        return LIST_WORDS;
    }

}
