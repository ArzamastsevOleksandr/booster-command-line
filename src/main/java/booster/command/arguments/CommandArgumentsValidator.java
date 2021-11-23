package booster.command.arguments;

import booster.command.Command;
import booster.command.service.CommandArgumentsValidatorCollectionService;
import booster.parser.CommandLineInputTransformer;
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
