package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ImportArgs;
import com.booster.load.XlsxImportComponent;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportCommandHandler implements CommandHandler {

    private final CommandLineWriter commandLineWriter;

    private final XlsxImportComponent importComponent;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (ImportArgs) commandWithArguments.getArgs();
            importComponent.load(args.getFilename());
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.IMPORT;
    }

}
