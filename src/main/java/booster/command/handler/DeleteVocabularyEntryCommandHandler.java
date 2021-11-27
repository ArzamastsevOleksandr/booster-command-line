package booster.command.handler;

import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.DeleteVocabularyEntryCommandArgs;
import booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteVocabularyEntryCommandHandler implements CommandHandler {

    private final VocabularyEntryService vocabularyEntryService;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (DeleteVocabularyEntryCommandArgs) commandArgs;
        vocabularyEntryService.delete(args.id());
    }

    @Override
    public Command getCommand() {
        return Command.DELETE_VOCABULARY_ENTRY;
    }

}
