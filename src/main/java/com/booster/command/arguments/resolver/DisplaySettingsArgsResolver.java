package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.booster.command.Command.DISPLAY_SETTINGS;

@Component
@RequiredArgsConstructor
public class DisplaySettingsArgsResolver implements ArgsResolver {

    // todo: a default method
    @Override
    public CommandWithArguments resolve(List<String> args) {
        return getCommandBuilder().build();
    }

    @Override
    public Command command() {
        return DISPLAY_SETTINGS;
    }

}
