package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.AddTagCommandArgs;
import booster.command.arguments.CommandArgs;
import booster.model.Tag;
import booster.service.TagService;
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
