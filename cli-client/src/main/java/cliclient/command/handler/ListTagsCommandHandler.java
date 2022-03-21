package cliclient.command.handler;

import api.tags.TagDto;
import api.tags.TagsApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListTagsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final TagsApi tagsApi;

    @Override
    public void handle(CommandArgs commandArgs) {
        Collection<TagDto> tags = tagsApi.findAll();
        if (CollectionUtils.isEmpty(tags)) {
            adapter.error("No records");
        } else {
            tags.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_TAGS;
    }

}
