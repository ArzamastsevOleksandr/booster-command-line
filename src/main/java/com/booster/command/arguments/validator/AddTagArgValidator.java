package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.booster.command.Command.ADD_TAG;

@Component
@RequiredArgsConstructor
public class AddTagArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        Optional<String> optionalName = commandWithArgs.getName();
        if (optionalName.isEmpty()) {
            throw new ArgsValidationException("Name is missing");
        }
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return ADD_TAG;
    }

}
