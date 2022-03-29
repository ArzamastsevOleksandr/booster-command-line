package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.FlagType;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.ListFlagTypesCmdWithArgs;
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
    public void handle(CmdArgs cwa) {
        Arrays.stream(FlagType.values())
                .filter(FlagType::isKnown)
                .map(colorProcessor::coloredFlagType)
                .forEach(adapter::writeLine);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return ListFlagTypesCmdWithArgs.class;
    }

}
