package com.booster.command.arguments;

import com.booster.command.Command;
import com.booster.command.service.CommandArgumentsValidatorCollectionService;
import com.booster.parser.CommandLineInputTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandArgumentsValidator {

    private final CommandArgumentsValidatorCollectionService commandArgumentsValidatorCollectionService;

    private final CommandLineInputTransformer transformer;

    public CommandWithArgs validate(String line) {
        CommandWithArgs commandWithArgs = transformer.fromString(line);
        if (Command.isExit(commandWithArgs.getCommand())) {
            return commandWithArgs;
        } else if (commandWithArgs.hasNoErrors()) {
            return commandArgumentsValidatorCollectionService.validate(commandWithArgs);
        }
        return commandWithArgs;
    }

}
