package cliclient.command.arguments.validator;

import cliclient.command.Command;
import cliclient.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static cliclient.command.Command.DOWNLOAD;

@Component
@RequiredArgsConstructor
// todo: validator validates (SRP, separation of concerns, use preprocessor)
public class DownloadArgValidator implements ArgValidator {

    private static final String XLSX = ".xlsx";
    private static final String DEFAULT_DOWNLOAD_FILE = "download" + XLSX;

    @Override
    public CommandWithArgs validateAndReturn(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.getFilename().isEmpty()) {
            return commandWithArgs.toBuilder().filename(formatFilename(DEFAULT_DOWNLOAD_FILE)).build();
        }
        return commandWithArgs;
    }

    @Override
    public Command command() {
        return DOWNLOAD;
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
