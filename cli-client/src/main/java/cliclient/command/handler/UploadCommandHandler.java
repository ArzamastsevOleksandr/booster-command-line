package cliclient.command.handler;

import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.UploadCommandArgs;
import cliclient.load.ExcelUploadComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UploadCommandHandler implements CommandHandler {

    private final ExcelUploadComponent uploadComponent;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UploadCommandArgs) commandArgs;
        uploadComponent.load(args.filename());
    }

    @Override
    public Command getCommand() {
        return Command.UPLOAD;
    }

}
