package booster.command.arguments.validator;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static booster.command.Command.LIST_TAGS;

@Component
public class ListTagsArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return LIST_TAGS;
    }

}
