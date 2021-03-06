package cliclient.command.handler;

import api.tags.CreateTagInput;
import api.tags.TagDto;
import api.tags.TagsApi;
import cliclient.adapter.CommandLineAdapter;
import cliclient.command.args.AddTagCmdArgs;
import cliclient.command.args.CmdArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddTagCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final TagsApi tagsApi;

    @Override
    public void handle(CmdArgs cmdArgs) {
        var args = (AddTagCmdArgs) cmdArgs;
        TagDto tagDto = tagsApi.create(new CreateTagInput(args.name()));
        adapter.writeLine(tagDto);
    }

    @Override
    public Class<? extends CmdArgs> getCmdArgsClass() {
        return AddTagCmdArgs.class;
    }

}
