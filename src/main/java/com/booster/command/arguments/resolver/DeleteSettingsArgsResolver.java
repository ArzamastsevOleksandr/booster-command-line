package com.booster.command.arguments.resolver;

import com.booster.command.Command;
import com.booster.command.arguments.CommandWithArguments;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.booster.command.Command.DELETE_SETTINGS;

@Component
public class DeleteSettingsArgsResolver implements ArgsResolver {

    @Override
    public CommandWithArguments resolve(List<String> args) {
        return getCommandBuilder().build();
    }

    @Override
    public Command command() {
        return DELETE_SETTINGS;
    }

}
