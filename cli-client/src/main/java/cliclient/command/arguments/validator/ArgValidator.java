package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;

public interface ArgValidator {

    Runnable ID_IS_MISSING = () -> {
        throw new ArgsValidationException("Id is missing");
    };

    Runnable NAME_IS_MISSING = () -> {
        throw new ArgsValidationException("Name is missing");
    };

    default CommandWithArgs validate(CommandWithArgs commandWithArgs) {
        try {
            return validateAndReturn(commandWithArgs);
        } catch (ArgsValidationException e) {
            return getCommandWithArgumentsBuilder().errors(e.errors).build();
        }
    }

    CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs);

    Command command();

    default CommandWithArgs.CommandWithArgsBuilder getCommandWithArgumentsBuilder() {
        return CommandWithArgs.builder()
                .command(command());
    }

}
