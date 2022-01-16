package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ExportCommandArgs;
import cliclient.export.XlsxExportComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExportCommandHandler implements CommandHandler {

    private final XlsxExportComponent exportComponent;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ExportCommandArgs) commandArgs;
        exportComponent.export(args.filename());
    }

    @Override
    public Command getCommand() {
        return Command.EXPORT;
    }

}
