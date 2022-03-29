package cliclient.command.service;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.ErroneousCmdArgs;
import cliclient.command.args.HelpCmdArgs;
import cliclient.command.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Service
public class CommandHandlerCollectionService {

    private final Map<Class<? extends CmdArgs>, CommandHandler> command2Handler;
    private final CommandLineAdapter adapter;

    @Autowired
    public CommandHandlerCollectionService(List<CommandHandler> commandHandlers, CommandLineAdapter adapter) {
        this.adapter = adapter;
        this.command2Handler = commandHandlers.stream()
                .map(ch -> Map.entry(ch.getCmdArgsClass(), ch))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void handle(CmdArgs cmdArgs) {
        if (cmdArgs.isNoInput()) {
            return;
        } else if (cmdArgs.hasNoErrors()) {
            handleCommandWithArgs(cmdArgs);
        } else {
            var erroneous = (ErroneousCmdArgs) cmdArgs;
            adapter.error(erroneous.error());
            if (erroneous.getCommand() != null) {
                this.handleCommandWithArgs(new HelpCmdArgs(erroneous.getCommand()));
            }
        }
        adapter.newLine();
    }

    private void handleCommandWithArgs(CmdArgs cmdArgs) {
        ofNullable(command2Handler.get(cmdArgs.getClass())).ifPresentOrElse(
                commandHandler -> commandHandler.handle(cmdArgs),
                () -> adapter.error("No handler is present for the " + cmdArgs.getClass().getSimpleName() + " arguments.")
        );
    }

}
