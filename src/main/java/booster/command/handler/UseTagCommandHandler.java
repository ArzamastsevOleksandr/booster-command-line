package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandArgs;
import booster.command.arguments.UseTagCommandArgs;
import booster.dao.params.AddTagToVocabularyEntryDaoParams;
import booster.model.Note;
import booster.model.VocabularyEntry;
import booster.service.NoteService;
import booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UseTagCommandHandler implements CommandHandler {

    private final NoteService noteService;
    private final VocabularyEntryService vocabularyEntryService;
    private final CommandLineAdapter adapter;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (UseTagCommandArgs) commandArgs;
        args.noteId().ifPresent(id -> {
            Note note = noteService.addTag(args.tag(), id);
            adapter.writeLine(note);
        });
        args.vocabularyEntryId().ifPresent(id -> {
            VocabularyEntry vocabularyEntry = vocabularyEntryService.addTag(new AddTagToVocabularyEntryDaoParams(args.tag(), id));
            adapter.writeLine(vocabularyEntry);
        });
    }

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
