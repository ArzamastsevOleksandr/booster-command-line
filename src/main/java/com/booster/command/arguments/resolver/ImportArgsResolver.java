package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ImportArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.booster.command.Command.IMPORT;

@Component
@RequiredArgsConstructor
public class ImportArgsResolver implements ArgsResolver {

    private static final String FILE_FLAG = "f";
    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_IMPORT_FILE = "import" + XLSX;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            if (flag2value.isEmpty()) {
                checkDefaultImportFileExists();
                return builder.args(new ImportArgs(DEFAULT_IMPORT_FILE)).build();
            }
            checkIfMandatoryFlagsArePresent(flag2value, Set.of(FILE_FLAG));
            checkCustomFileExists(flag2value.get(FILE_FLAG));
            return builder
                    .args(new ImportArgs(flag2value.get(FILE_FLAG)))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    private void checkDefaultImportFileExists() {
        File file = new File(DEFAULT_IMPORT_FILE);
        if (!file.exists() || file.isDirectory()) {
            throw new ArgsValidationException(List.of(
                    "Default import file not found: " + DEFAULT_IMPORT_FILE,
                    "Try specifying custom filename")
            );
        }
    }

    private void checkCustomFileExists(String filename) {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            throw new ArgsValidationException(List.of("Custom import file not found: " + DEFAULT_IMPORT_FILE));
        }
    }

    @Override
    public Command command() {
        return IMPORT;
    }

}
