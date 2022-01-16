package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.ImportCommandArgs;
import cliclient.load.XlsxImportComponent;
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
