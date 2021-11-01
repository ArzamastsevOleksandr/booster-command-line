package com.booster.command.arguments.validator;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.booster.command.Command.EXPORT;

@Component
@RequiredArgsConstructor
public class ExportArgValidator implements ArgValidator {

    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_EXPORT_FILE = "export" + XLSX;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getFilename().isEmpty()) {
            return commandWithArgs.toBuilder().filename(formatFilename(DEFAULT_EXPORT_FILE)).build();
        }
        return commandWithArgs;
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
