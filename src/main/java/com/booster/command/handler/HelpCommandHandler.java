package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandWithArgs commandWithArgs) {
        if (commandWithArgs.hasNoErrors()) {

            adapter.newLine();
            adapter.writeLine("Available commands are:");
            adapter.newLine();

            Arrays.stream(Command.values())
                    .filter(Command::isRecognizable)
                    .forEach(command -> adapter.writeLine(command.toString() + ": " + command.getEquivalents()));

            adapter.newLine();
        } else {
            adapter.writeLine("Errors: ");
            adapter.newLine();
            commandWithArgs.getErrors()
                    .forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
