package cliclient.command.handler;

import api.tags.CreateTagInput;
import api.tags.TagDto;
import api.tags.TagsApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.AddTagCommandArgs;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final TagsApi tagsApi;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (AddTagCommandArgs) commandArgs;
        TagDto tagDto = tagsApi.create(new CreateTagInput(args.name()));
        adapter.writeLine(tagDto);
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
