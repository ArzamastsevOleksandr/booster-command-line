package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.load.XlsxImportComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    private final XlsxImportComponent importComponent;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        if (commandWithArguments.hasNoErrors()) {
            commandWithArguments.getFilename().ifPresent(importComponent::load);
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.IMPORT;
    }

}
