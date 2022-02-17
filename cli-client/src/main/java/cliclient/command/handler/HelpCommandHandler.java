package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.service.ColorProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;

    // todo: help with pagination + help --all
    @Override
    public void handle(CommandArgs commandArgs) {
        Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .map(colorProcessor::coloredCommand)
                .forEach(adapter::writeLine);
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
