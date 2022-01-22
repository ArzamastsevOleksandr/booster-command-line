package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import cliclient.service.LanguageService;
import cliclient.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.ADD_SETTINGS;

// todo: functional style error processing with no exceptions (Option).
//  have a list of validators that return an Option[], collect all errors
@Component
@RequiredArgsConstructor
public class AddSettingsArgValidator implements ArgValidator {

    private final LanguageService languageService;
    private final SettingsService settingsService;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
//        checkIfSettingsAlreadyExist();
//
//        commandWithArgs.getLanguageId().ifPresent(this::checkIfLanguageExistsWithId);

        return commandWithArgs;
    }

    @Override
    public Command command() {
        return ADD_SETTINGS;
    }

    private void checkIfSettingsAlreadyExist() {
        if (settingsService.existAny()) {
            throw new ArgsValidationException("Settings already exist");
        }
    }

    private void checkIfLanguageExistsWithId(long id) {
        if (!languageService.existsWithId(id)) {
            throw new ArgsValidationException("Language with id: " + id + " does not exist");
        }
    }

}
