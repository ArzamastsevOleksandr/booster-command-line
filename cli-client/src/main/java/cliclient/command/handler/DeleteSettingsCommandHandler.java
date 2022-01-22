package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteSettingsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        adapter.writeLine("NOT IMPLEMENTED");
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_SETTINGS;
    }

}
