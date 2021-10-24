package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ExportArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.booster.command.Command.EXPORT;

@Component
@RequiredArgsConstructor
public class ExportArgsResolver implements ArgsResolver {

    private static final String FILE_FLAG = "f";
    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_EXPORT_FILE = "export" + XLSX;

    @Override
    public CommandWithArguments resolve(List<String> args) {
        CommandWithArguments.CommandWithArgumentsBuilder builder = getCommandBuilder();
        try {
            Map<String, String> flag2value = checkFlagsWithValuesAndReturn(args);
            if (flag2value.isEmpty()) {
                return builder.args(new ExportArgs(DEFAULT_EXPORT_FILE)).build();
            }
            checkIfMandatoryFlagsArePresent(flag2value, Set.of(FILE_FLAG));

            return builder
                    .args(new ExportArgs(formatFilename(flag2value.get(FILE_FLAG))))
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return filename + "_" + timestamp;
    }

}
