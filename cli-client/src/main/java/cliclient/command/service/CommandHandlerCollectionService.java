package cliclient.command.service;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.args.ErroneousCmdArgs;
import cliclient.command.args.HelpCmdArgs;
import cliclient.command.args.CmdArgs;
import cliclient.command.handler.CommandHandler;
import cliclient.service.ColorProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Service
public class CommandHandlerCollectionService {

    private final ColorProcessor colorProcessor;
    private final Map<Command, CommandHandler> command2Handler;
    private final CommandLineAdapter adapter;

    @Autowired
    public CommandHandlerCollectionService(ColorProcessor colorProcessor,
                                           List<CommandHandler> commandHandlers,
                                           CommandLineAdapter adapter) {
        this.colorProcessor = colorProcessor;
        this.adapter = adapter;
        this.command2Handler = commandHandlers.stream()
                .map(ch -> Map.entry(ch.getCommand(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void handle(CmdArgs cmdArgs) {
        if (cmdArgs.getCommand() == Command.NO_INPUT) {
            return;
        } else if (cmdArgs.hasNoErrors()) {
            handleCommandWithArgs(cmdArgs);
        } else {
            adapter.error(((ErroneousCmdArgs) cmdArgs).getError());
            if (cmdArgs.getCommand() != null) {
                this.handleCommandWithArgs(new HelpCmdArgs(cmdArgs.getCommand()));
            }
        }
        adapter.newLine();
    }

    private void handleCommandWithArgs(CmdArgs cmdArgs) {
        Command command = cmdArgs.getCommand();
        ofNullable(command2Handler.get(command)).ifPresentOrElse(
                commandHandler -> commandHandler.handle(cmdArgs),
                () -> adapter.error("No handler is present for the " + colorProcessor.coloredCommand(command) + " command.")
        );
    }

}
