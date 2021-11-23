package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import booster.export.XlsxExportComponent;
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
