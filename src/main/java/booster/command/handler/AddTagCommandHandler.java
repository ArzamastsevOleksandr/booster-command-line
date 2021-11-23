package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
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
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getName().ifPresent(name -> {
            Tag tag = tagService.add(name);
            adapter.writeLine(tag);
        });
    }

    @Override
    public Command getCommand() {
        return Command.ADD_TAG;
    }

}
