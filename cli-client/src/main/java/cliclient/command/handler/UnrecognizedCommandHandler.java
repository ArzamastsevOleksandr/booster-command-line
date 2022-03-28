package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.CmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Deprecated
@Component
@RequiredArgsConstructor
public class UnrecognizedCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CmdArgs cwa) {
        adapter.writeLine("Unknown command.");
        adapter.newLine();
        adapter.help();
    }

    @Override
    public Command getCommand() {
        return Command.UNRECOGNIZED;
    }

}
