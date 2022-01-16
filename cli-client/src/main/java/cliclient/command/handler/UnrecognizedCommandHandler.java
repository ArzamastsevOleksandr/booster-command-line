package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.adapter.CommonOperations;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnrecognizedCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final CommonOperations commonOperations;

    @Override
    public void handle(CommandArgs commandArgs) {
        adapter.writeLine("Unknown command.");
        adapter.newLine();
        commonOperations.help();
    }

    @Override
    public Command getCommand() {
        return Command.UNRECOGNIZED;
    }

}
