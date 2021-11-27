package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.model.Tag;
import booster.service.TagService;
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
