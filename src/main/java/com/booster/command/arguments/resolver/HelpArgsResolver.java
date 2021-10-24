package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import com.booster.command.arguments.HelpArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.booster.command.Command.HELP;

@Component
@RequiredArgsConstructor
public class HelpArgsResolver implements ArgsResolver {

    @Override
    public CommandWithArguments resolve(List<String> args) {
        var builder = getCommandBuilder();
        try {
            return builder.args(new HelpArgs())
                    .build();
        } catch (ArgsValidationException e) {
            return builder
                    .argErrors(e.getArgErrors())
                    .build();
        }
    }

    @Override
    public Command command() {
        return HELP;
    }

}
