package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.HelpArgs;
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
        if (commandWithArguments.hasNoErrors()) {
            var args = (HelpArgs) commandWithArguments.getArgs();

            commandLineWriter.newLine();
            commandLineWriter.writeLine("Available commands are:");
            commandLineWriter.newLine();

            Arrays.stream(Command.values())
                    .filter(Command::isRecognizable)
                    .forEach(command -> commandLineWriter.writeLine(command.toString() + ": " + command.getEquivalents()));

            commandLineWriter.newLine();
        } else {
            commandLineWriter.writeLine("Errors: ");
            commandLineWriter.newLine();
            commandWithArguments.getArgErrors()
                    .forEach(commandLineWriter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
