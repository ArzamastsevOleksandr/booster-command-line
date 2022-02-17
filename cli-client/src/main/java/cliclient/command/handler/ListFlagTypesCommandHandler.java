package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.CommandArgs;
import cliclient.service.ColorProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class ListFlagTypesCommandHandler implements CommandHandler {

    private final ColorProcessor colorProcessor;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        Arrays.stream(FlagType.values())
                .filter(FlagType::isKnown)
                .map(colorProcessor::coloredFlagType)
                .forEach(adapter::writeLine);
    }

    @Override
    public Command getCommand() {
        return Command.LIST_FLAG_TYPES;
    }

}
