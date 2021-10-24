package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ExportArgs;
import com.booster.export.XlsxExportComponent;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExportCommandHandler implements CommandHandler {

    private final CommandLineWriter commandLineWriter;

    private final XlsxExportComponent exportComponent;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (ExportArgs) commandWithArguments.getArgs();
            exportComponent.export(args.getFilename());
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.EXPORT;
    }

}
