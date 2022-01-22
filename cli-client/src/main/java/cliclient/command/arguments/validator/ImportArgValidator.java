package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

import static cliclient.command.Command.IMPORT;

@Component
@RequiredArgsConstructor
public class ImportArgValidator implements ArgValidator {

    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_IMPORT_FILE = "import" + XLSX;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
//        Optional<String> filename = commandWithArgs.getFilename();
//        if (filename.isPresent()) {
//            checkCustomFileExists(filename.get());
//            return commandWithArgs;
//        } else {
//            checkDefaultImportFileExists();
//        }
        return commandWithArgs.toBuilder().filename(DEFAULT_IMPORT_FILE).build();
    }

    private void checkDefaultImportFileExists() {
        File file = new File(DEFAULT_IMPORT_FILE);
        if (!file.exists() || file.isDirectory()) {
            throw new ArgsValidationException(
                    "Default import file not found: " + DEFAULT_IMPORT_FILE,
                    "Try specifying custom filename"
            );
        }
    }

    private void checkCustomFileExists(String filename) {
        File file = new File(filename);
        // todo: fix error
        if (!file.exists() || file.isDirectory()) {
            throw new ArgsValidationException("Custom import file not found: " + DEFAULT_IMPORT_FILE);
        }
    }

    @Override
    public Command command() {
        return IMPORT;
    }

}
