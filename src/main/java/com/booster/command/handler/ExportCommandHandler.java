package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.ExportArgs;
import com.booster.export.XlsxExportComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExportCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    private final XlsxExportComponent exportComponent;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            var args = (ExportArgs) commandWithArguments.getArgs();
            exportComponent.export(args.getFilename());
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.EXPORT;
    }

}
