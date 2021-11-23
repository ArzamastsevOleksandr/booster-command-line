package booster.command.handler;

import booster.adapter.CommandLineAdapter;
import booster.command.Command;
import booster.command.arguments.CommandWithArgs;
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
    public void handle(CommandWithArgs commandWithArgs) {
        commandWithArgs.getTag().ifPresent(tag -> {
            commandWithArgs.getNoteId().ifPresent(noteId -> {
                Note note = noteService.addTag(tag, noteId);
                adapter.writeLine(note);
            });
            commandWithArgs.getVocabularyEntryId().ifPresent(veId -> {
                VocabularyEntry vocabularyEntry = vocabularyEntryService.addTag(new AddTagToVocabularyEntryDaoParams(tag, veId));
                adapter.writeLine(vocabularyEntry);
            });
        });
    }

    @Override
    public Command getCommand() {
        return Command.USE_TAG;
    }

}
