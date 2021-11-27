package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.ImportCommandArgs;
import booster.load.XlsxImportComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportCommandHandler implements CommandHandler {

    private final XlsxImportComponent importComponent;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (ImportCommandArgs) commandArgs;
        importComponent.load(args.filename());
    }

    @Override
    public Command getCommand() {
        return Command.IMPORT;
    }

}
