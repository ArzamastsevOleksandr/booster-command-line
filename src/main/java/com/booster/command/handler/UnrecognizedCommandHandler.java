package com.booster.command.handler;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.output.CommandLineWriter;
import com.booster.output.CommonOperations;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnrecognizedCommandHandler implements CommandHandler {

    private final CommandLineWriter commandLineWriter;

    private final CommonOperations commonOperations;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        commandLineWriter.writeLine("Unknown command.");
        commandLineWriter.newLine();
        commonOperations.help();
    }

    @Override
    public Command getCommand() {
        return Command.UNRECOGNIZED;
    }

}
