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

    public CommandWithArguments validate(String line) {
        CommandWithArguments commandWithArguments = transformer.fromString(line);
        if (Command.isExit(commandWithArguments.getCommand())) {
            return commandWithArguments;
        }
        return commandArgumentsValidatorCollectionService.validate(commandWithArguments);
    }

}
