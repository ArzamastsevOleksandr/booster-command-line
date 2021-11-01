package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.EXPORT;

@Component
@RequiredArgsConstructor
public class ExportArgValidator implements ArgValidator {

    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_EXPORT_FILE = "export" + XLSX;

    @Override
    public CommandWithArguments validate(CommandWithArguments commandWithArguments) {
        try {
            if (commandWithArguments.getFilename().isEmpty()) {
                return commandWithArguments.toBuilder().filename(formatFilename(DEFAULT_EXPORT_FILE)).build();
            }
            return commandWithArguments;
        } catch (ArgsValidationException e) {
            return getCommandBuilder().argErrors(e.errors).build();
        }
    }

    @Override
    public Command command() {
        return EXPORT;
    }

    private String formatFilename(String filename) {
        if (filename.endsWith(XLSX)) {
            return addDateToFilename(filename.replace(XLSX, "")) + XLSX;
        }
        return addDateToFilename(filename) + XLSX;
    }

    private String addDateToFilename(String filename) {
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//        return filename + "_" + timestamp;
        return filename;
    }

}
