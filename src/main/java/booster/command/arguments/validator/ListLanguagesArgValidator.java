package booster.command.arguments.validator;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static booster.command.Command.LIST_LANGUAGES;

@Component
public class ListLanguagesArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return LIST_LANGUAGES;
    }

}
