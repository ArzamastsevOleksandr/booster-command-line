package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.ExportCommandArgs;
import booster.export.XlsxExportComponent;
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
