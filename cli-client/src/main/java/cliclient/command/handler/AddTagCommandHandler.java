package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddTagCommandArgs;
import cliclient.command.arguments.CommandArgs;
import cliclient.model.Tag;
import cliclient.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagCommandHandler implements CommandHandler {

    private final TagService tagService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddTagCommandArgs) commandArgs;
        Tag tag = tagService.add(args.name());
        adapter.writeLine(tag);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
