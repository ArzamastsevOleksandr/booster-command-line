package com.booster.command.handler;

import com.booster.adapter.CommandLineAdapter;
import com.booster.adapter.CommonOperations;
import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnrecognizedCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;

    private final CommonOperations commonOperations;

    @Override
    public void handle(CommandWithArguments commandWithArguments) {
        adapter.writeLine("Unknown command.");
        adapter.newLine();
        commonOperations.help();
    }

    @Override
    public Command getCommand() {
        return Command.UNRECOGNIZED;
    }

}
