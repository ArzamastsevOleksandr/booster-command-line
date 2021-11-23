package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
import booster.load.XlsxImportComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportCommandHandler implements CommandHandler {

    private final XlsxImportComponent importComponent;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getFilename().ifPresent(importComponent::load);
    }

    @Override
    public Command getCommand() {
        return Command.IMPORT;
    }

}
