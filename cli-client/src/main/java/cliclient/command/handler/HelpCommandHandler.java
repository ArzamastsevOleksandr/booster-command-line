package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        adapter.writeLine("Available commands are:");
        adapter.newLine();

        Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .map(Command::extendedToString)
                .forEach(adapter::writeLine);
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
