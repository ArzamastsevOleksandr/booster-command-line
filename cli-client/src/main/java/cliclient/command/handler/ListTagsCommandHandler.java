package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.model.Tag;
import cliclient.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListTagsCommandHandler implements CommandHandler {

    private final TagService tagService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        List<Tag> tags = tagService.findAll();

        if (tags.isEmpty()) {
            adapter.writeLine("There are no tags in the system now.");
        } else {
            adapter.writeLine("All tags:");
            adapter.newLine();

            tags.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_TAGS;
    }

}
