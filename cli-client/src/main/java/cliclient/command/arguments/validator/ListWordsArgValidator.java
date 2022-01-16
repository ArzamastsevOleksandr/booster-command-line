package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.LIST_WORDS;

@Component
public class ListWordsArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return LIST_WORDS;
    }

}
