package cliclient.command.arguments;

import cliclient.command.Command;
import cliclient.command.service.CommandArgumentsValidatorCollectionService;
import cliclient.parser.CommandLineInputTransformer;
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
