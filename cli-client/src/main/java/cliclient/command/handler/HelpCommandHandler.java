package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.HelpCommandArgs;
import cliclient.service.ColorProcessor;
import cliclient.util.ColorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;

    // todo: help with pagination + help --all
    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (HelpCommandArgs) commandArgs;
        args.getHelpTarget().ifPresentOrElse(this::helpForTarget, () -> Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .map(colorProcessor::coloredCommand)
                .forEach(adapter::writeLine));
    }

    private void helpForTarget(Command command) {
        String commandDescription = switch (command) {
            case HELP -> """
                    [%s]:     Help on the global command system
                    [%s] [c]: Help on a specific command usage, where [c] is a command
                              Example:
                                      %s %s:  get help on the [%s] command
                    """.formatted(
                    colorProcessor.coloredCommand(Command.HELP),
                    ColorCodes.green(Command.HELP.firstEquivalent()),
                    ColorCodes.green(String.join(", ", Command.HELP.firstEquivalent())),
                    ColorCodes.green(String.join(", ", Command.ADD_VOCABULARY_ENTRY.firstEquivalent())),
                    colorProcessor.coloredCommand(Command.ADD_VOCABULARY_ENTRY)
            );
            case LIST_LANGUAGES -> """
                    [%s]: List all languages being learned
                    """.formatted(colorProcessor.coloredCommand(Command.LIST_LANGUAGES));
            case ADD_LANGUAGE -> """
                    [%s]: Add a new language to be learned
                          Example:
                                 %s \\%s=English
                    """.formatted(
                    colorProcessor.coloredCommand(Command.ADD_LANGUAGE),
                    ColorCodes.green(String.join(", ", Command.ADD_LANGUAGE.firstEquivalent())),
                    ColorCodes.green(FlagType.NAME.value)
            );
            default -> throw new RuntimeException("Help not implemented for: " + command);
        };
        adapter.writeLine(commandDescription);
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
