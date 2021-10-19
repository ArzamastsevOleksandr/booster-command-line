package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final CommandLineWriter commandLineWriter;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        commandLineWriter.writeLine("Available commands are:");
        commandLineWriter.newLine();

        Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .forEach(command -> commandLineWriter.writeLine(command.toString() + ": " + command.getEquivalents()));

        commandLineWriter.newLine();
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
