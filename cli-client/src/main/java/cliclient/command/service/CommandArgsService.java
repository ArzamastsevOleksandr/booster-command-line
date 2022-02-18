package cliclient.command.service;

import api.settings.SettingsDto;
import cliclient.command.FlagType;
import cliclient.command.arguments.*;
import cliclient.feign.settings.SettingsServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
class CommandArgsService {

    private final SettingsServiceClient settingsServiceClient;

    @Value("${upload.filename:upload.xlsx}")
    private String uploadFilename;
    @Value("${download.filename:download.xlsx}")
    private String downloadFilename;

    CommandArgs getCommandArgs(CommandWithArgs cwa) {
        return switch (cwa.getCommand()) {
            // todo: paginated languages and tags
            case EXIT, NO_INPUT, LIST_FLAG_TYPES, LIST_LANGUAGES, DELETE_SETTINGS, LIST_TAGS, SHOW_SETTINGS, UNRECOGNIZED -> new EmptyCommandArgs();
            case HELP -> new HelpCommandArgs(cwa.getHelpTarget());
            case ADD_LANGUAGE -> new AddLanguageCommandArgs(cwa.getName());
            case DELETE_LANGUAGE -> new DeleteLanguageCommandArgs(cwa.getId());
            case LIST_VOCABULARY_ENTRIES -> new ListVocabularyEntriesCommandArgs(ofNullable(cwa.getId()), getVocabularyPagination(cwa), ofNullable(cwa.getSubstring()));
            case DELETE_VOCABULARY_ENTRY -> new DeleteVocabularyEntryCommandArgs(cwa.getId());
            case ADD_VOCABULARY_ENTRY -> AddVocabularyEntryCommandArgs.builder()
                    .name(cwa.getName())
                    .languageId(cwa.getLanguageId())
                    .definition(cwa.getDefinition())
                    .tag(cwa.getTag())
                    .antonyms(cwa.getAntonyms())
                    .synonyms(cwa.getSynonyms())
                    .contexts(cwa.getContexts())
                    .build();
            case UPDATE_VOCABULARY_ENTRY -> UpdateVocabularyEntryCommandArgs.builder()
                    .id(cwa.getId())
                    .name(cwa.getName())
                    .definition(cwa.getDefinition())
                    .correctAnswersCount(cwa.getCorrectAnswersCount())
                    .antonyms(cwa.getAntonyms())
                    .synonyms(cwa.getSynonyms())
                    .addAntonyms(cwa.getAddAntonyms())
                    .removeAntonyms(cwa.getRemoveAntonyms())
                    .addSynonyms(cwa.getAddSynonyms())
                    .removeSynonyms(cwa.getRemoveSynonyms())
                    .build();
            case START_VOCABULARY_TRAINING_SESSION -> new StartVocabularyTrainingSessionCommandArgs(cwa.getMode());
            case DOWNLOAD -> new DownloadCommandArgs(ofNullable(cwa.getFilename()).orElse(downloadFilename));
            case UPLOAD -> new UploadCommandArgs(ofNullable(cwa.getFilename()).orElse(uploadFilename));
            case ADD_SETTINGS -> AddSettingsCommandArgs.builder()

                    .defaultLanguageId(cwa.getLanguageId())
                    .defaultLanguageName(cwa.getLanguageName())

                    .entriesPerVocabularyTrainingSession(cwa.getEntriesPerVocabularyTrainingSession())

                    .languagesPagination(cwa.getLanguagesPagination())
                    .notesPagination(cwa.getNotesPagination())
                    .tagsPagination(cwa.getTagsPagination())
                    .vocabularyPagination(cwa.getVocabularyPagination())

                    .build();
            case LIST_NOTES -> new ListNotesCommandArgs(ofNullable(cwa.getId()), getNotesPagination(cwa));
            case ADD_NOTE -> new AddNoteCommandArgs(cwa.getContent(), ofNullable(cwa.getTag()).map(Set::of).orElse(Set.of()));
            case DELETE_NOTE -> new DeleteNoteCommandArgs(cwa.getId());
            case ADD_TAG -> new AddTagCommandArgs(cwa.getName());
            case USE_TAG -> useTag(cwa);
            case MARK_VOCABULARY_ENTRY_DIFFICULT, MARK_VOCABULARY_ENTRY_NOT_DIFFICULT -> new MarkVocabularyEntryDifficultCommandArgs(cwa.getId());
        };
    }

    // todo: fetch settings once and use the cached version
    private Integer getVocabularyPagination(CommandWithArgs cwa) {
        return findSettingsIgnoringNotFound()
                .map(SettingsDto::getVocabularyPagination)
                .orElse(cwa.getPagination());
    }

    private Integer getNotesPagination(CommandWithArgs cwa) {
        return findSettingsIgnoringNotFound()
                .map(SettingsDto::getNotesPagination)
                .orElse(cwa.getPagination());
    }

    private Optional<SettingsDto> findSettingsIgnoringNotFound() {
        try {
            return Optional.of(settingsServiceClient.findOne());
        } catch (FeignException.FeignClientException ex) {
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }
            throw ex;
        }
    }

    private UseTagCommandArgs useTag(CommandWithArgs cmdWithArgs) {
        checkThatAnyTargetIsPresent(cmdWithArgs);
        return new UseTagCommandArgs(cmdWithArgs.getTag(), ofNullable(cmdWithArgs.getNoteId()), ofNullable(cmdWithArgs.getVocabularyEntryId()));
    }

    // todo: find a place for validation
    private void checkThatAnyTargetIsPresent(CommandWithArgs commandWithArgs) {
        boolean noTargetsArePresent = Stream.of(commandWithArgs.getVocabularyEntryId(), commandWithArgs.getNoteId())
                .allMatch(Objects::nonNull);

        if (noTargetsArePresent) {
            String flagTypes = Stream.of(FlagType.NOTE_ID, FlagType.VOCABULARY_ENTRY_ID)
                    .map(f -> f + "(" + f.value + ")")
                    .collect(joining(", "));
//            throw new ArgsValidationException("At least one target must be specified when using tags: " + flagTypes);
        }
    }

}
