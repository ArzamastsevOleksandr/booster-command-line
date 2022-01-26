package cliclient.command.handler;

import api.tags.TagDto;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.arguments.CommandArgs;
import cliclient.feign.tags.TagServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class ListTagsCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final TagServiceClient tagServiceClient;

    @Override
    public void handle(CommandArgs commandArgs) {
        Collection<TagDto> tags = tagServiceClient.findAll();
        if (CollectionUtils.isEmpty(tags)) {
            adapter.writeLine("There are no tags yet");
        } else {
            tags.forEach(adapter::writeLine);
        }
    }

    @Override
    public Command getCommand() {
        return Command.LIST_TAGS;
    }

}
