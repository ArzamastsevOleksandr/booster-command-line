package cliclient.command.service;

import cliclient.command.FlagType;
import cliclient.command.arguments.*;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

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
                    .languageId(cmdWithArgs.getLanguageId())
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
            case START_VOCABULARY_TRAINING_SESSION -> new StartVocabularyTrainingSessionCommandArgs(cmdWithArgs.getMode().get());
            case DOWNLOAD -> new DownloadCommandArgs(cmdWithArgs.getDownloadFilename());
            case UPLOAD -> new UploadCommandArgs(cmdWithArgs.getUploadFilename());
            case ADD_SETTINGS -> new AddSettingsCommandArgs(cmdWithArgs.getLanguageId(), cmdWithArgs.getVocabularyTrainingSessionSize());
            case LIST_NOTES -> new ListNotesCommandArgs(cmdWithArgs.getId());
            case ADD_NOTE -> new AddNoteCommandArgs(cmdWithArgs.getContent().get(), cmdWithArgs.getTag().map(Set::of).orElse(Set.of()));
            case DELETE_NOTE -> new DeleteNoteCommandArgs(cmdWithArgs.getId().get());
            case ADD_TAG -> new AddTagCommandArgs(cmdWithArgs.getName().get());
            case USE_TAG -> useTag(cmdWithArgs);
            case MARK_VOCABULARY_ENTRY_DIFFICULT, MARK_VOCABULARY_ENTRY_NOT_DIFFICULT -> new MarkVocabularyEntryDifficultCommandArgs(cmdWithArgs.getId().get());
        };
    }

    private UseTagCommandArgs useTag(CommandWithArgs cmdWithArgs) {
        checkThatAnyTargetIsPresent(cmdWithArgs);
        return new UseTagCommandArgs(cmdWithArgs.getTag().get(), cmdWithArgs.getNoteId(), cmdWithArgs.getVocabularyEntryId());
    }

    // todo: find a place for validation
    private void checkThatAnyTargetIsPresent(CommandWithArgs commandWithArgs) {
        boolean noTargetsArePresent = Stream.of(commandWithArgs.getVocabularyEntryId(), commandWithArgs.getNoteId())
                .allMatch(Optional::isEmpty);

        if (noTargetsArePresent) {
            String flagTypes = Stream.of(FlagType.NOTE_ID, FlagType.VOCABULARY_ENTRY_ID)
                    .map(f -> f + "(" + f.value + ")")
                    .collect(joining(", "));
//            throw new ArgsValidationException("At least one target must be specified when using tags: " + flagTypes);
        }
    }

    private <T> T getOrNull(Supplier<Optional<T>> s) {
        return s.get().orElse(null);
    }

}
