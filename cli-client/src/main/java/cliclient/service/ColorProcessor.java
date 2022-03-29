package cliclient.service;

import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.util.ColorCodes;
import org.springframework.stereotype.Component;

@Component
public class ColorProcessor {

    public String coloredCommand(Command command) {
        return ColorCodes.cyan(command.name()) + ColorCodes.green(" (" + String.join(", ", command.getName()) + ")");
    }

    public String coloredFlagType(FlagType flagType) {
        return ColorCodes.cyan(flagType.name()) + ColorCodes.green(" (" + flagType.value + ")");
    }

}
