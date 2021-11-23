package booster.command.arguments.validator;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import org.springframework.stereotype.Component;

import static booster.command.Command.DELETE_SETTINGS;

@Component
public class DeleteSettingsArgValidator implements ArgValidator {

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return DELETE_SETTINGS;
    }

}
