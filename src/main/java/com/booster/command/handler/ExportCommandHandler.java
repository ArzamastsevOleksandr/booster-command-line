package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import com.booster.export.XlsxExportComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExportCommandHandler implements CommandHandler {

    private final XlsxExportComponent exportComponent;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getFilename().ifPresent(exportComponent::export);
    }

    @Override
    public Command getCommand() {
        return Command.EXPORT;
    }

}
