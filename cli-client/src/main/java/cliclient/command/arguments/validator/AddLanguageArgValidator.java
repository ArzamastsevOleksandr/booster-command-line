package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.ADD_LANGUAGE;

@Deprecated
@Component
public class AddLanguageArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return ADD_LANGUAGE;
    }

}
