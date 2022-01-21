package cliclient.command.service;

import cliclient.command.arguments.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
class CommandArgsService {

    CommandArgs getCommandArgs(CommandWithArgs cmdWithArgs) {
        return switch (cmdWithArgs.getCommand()) {
            case EXIT, HELP, LIST_LANGUAGES, DELETE_SETTINGS, LIST_TAGS, SHOW_SETTINGS, UNRECOGNIZED -> new EmptyCommandArgs();
            case ADD_LANGUAGE -> new AddLanguageCommandArgs(cmdWithArgs.getName().get());
            case DELETE_LANGUAGE -> new DeleteLanguageCommandArgs(cmdWithArgs.getId().get());
            case LIST_VOCABULARY_ENTRIES -> new ListVocabularyEntriesCommandArgs(cmdWithArgs.getId(), cmdWithArgs.getPagination(), cmdWithArgs.getSubstring());
            case DELETE_VOCABULARY_ENTRY -> new DeleteVocabularyEntryCommandArgs(cmdWithArgs.getId().get());
            case ADD_VOCABULARY_ENTRY -> AddVocabularyEntryCommandArgs.builder()
                    .name(cmdWithArgs.getName().get())
                    .languageId(getOrNull(cmdWithArgs::getLanguageId))
                    .definition(getOrNull(cmdWithArgs::getDefinition))
                    .tag(getOrNull(cmdWithArgs::getTag))
                    .antonyms(cmdWithArgs.getAntonyms())
                    .synonyms(cmdWithArgs.getSynonyms())
                    .contexts(cmdWithArgs.getContexts())
                    .build();
            case UPDATE_VOCABULARY_ENTRY -> UpdateVocabularyEntryCommandArgs.builder()
                    .id(cmdWithArgs.getId().get())
                    .name(getOrNull(cmdWithArgs::getName))
                    .definition(getOrNull(cmdWithArgs::getDefinition))
                    .correctAnswersCount(getOrNull(cmdWithArgs::getCorrectAnswersCount))
                    .antonyms(cmdWithArgs.getAntonyms())
                    .synonyms(cmdWithArgs.getSynonyms())
                    .addAntonyms(cmdWithArgs.getAddAntonyms())
                    .removeAntonyms(cmdWithArgs.getRemoveAntonyms())
                    .addSynonyms(cmdWithArgs.getAddSynonyms())
                    .removeSynonyms(cmdWithArgs.getRemoveSynonyms())
                    .build();
            case START_TRAINING_SESSION -> new StartTrainingSessionCommandArgs(cmdWithArgs.getMode().get());
            case EXPORT -> new ExportCommandArgs(cmdWithArgs.getFilename().get());
            case IMPORT -> new ImportCommandArgs(cmdWithArgs.getFilename().get());
            case ADD_SETTINGS -> new AddSettingsCommandArgs(getOrNull(cmdWithArgs::getLanguageId));
            case LIST_NOTES -> new ListNotesCommandArgs(cmdWithArgs.getId());
            case ADD_NOTE -> new AddNoteCommandArgs(cmdWithArgs.getContent().get(), cmdWithArgs.getTag().map(Set::of).orElse(Set.of()));
            case DELETE_NOTE -> new DeleteNoteCommandArgs(cmdWithArgs.getId().get());
            case ADD_TAG -> new AddTagCommandArgs(cmdWithArgs.getName().get());
            case USE_TAG -> new UseTagCommandArgs(cmdWithArgs.getTag().get(), cmdWithArgs.getNoteId(), cmdWithArgs.getVocabularyEntryId());
            case MARK_VOCABULARY_ENTRY_DIFFICULT, MARK_VOCABULARY_ENTRY_NOT_DIFFICULT -> new MarkVocabularyEntryDifficultCommandArgs(cmdWithArgs.getId().get());
        };
    }

    private <T> T getOrNull(Supplier<Optional<T>> s) {
        return s.get().orElse(null);
    }

}
