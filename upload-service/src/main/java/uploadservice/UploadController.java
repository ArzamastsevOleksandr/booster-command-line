package uploadservice;

import api.exception.XlsxStructureUnsupportedException;
import api.notes.AddNoteInput;
import api.notes.NoteApi;
import api.settings.CreateSettingsInput;
import api.settings.SettingsApi;
import api.tags.CreateTagInput;
import api.tags.TagsApi;
import api.upload.UploadApi;
import api.upload.UploadResponse;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.LanguageApi;
import api.vocabulary.VocabularyEntryApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

@Slf4j
@RestController
@RequestMapping(value = "/upload/")
@RequiredArgsConstructor
class UploadController implements UploadApi {

    private static final int HEADER_ROW_NUMBER = 0;

    private final SettingsApi settingsApi;
    private final TagsApi tagsApi;
    private final NoteApi noteApi;
    private final LanguageApi languageApi;
    private final VocabularyEntryApi vocabularyEntryApi;

    // todo: validation
    // todo: reliable way to get number of rows in xlsx
    @Override
    public UploadResponse upload(MultipartFile file) {
        try (var inputStream = file.getInputStream();
             var workbook = new XSSFWorkbook(inputStream)) {

            int numberOfSheets = workbook.getNumberOfSheets();

            var tracker = uploadProgressTracker();
            for (int i = 0; i < numberOfSheets; ++i) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName().strip().toLowerCase();

                switch (sheetName) {
                    case XlsxSheetName.SETTINGS -> importSettings(sheet, tracker);
                    case XlsxSheetName.TAGS -> importTags(sheet, tracker);
                    case XlsxSheetName.NOTES -> importNotes(sheet, tracker);
                    default -> {
                        if (sheetName.startsWith(XlsxSheetName.LANGUAGE)) {
                            importLanguage(sheet, tracker);
                        } else {
                            log.error("Unrecognized upload page ignored: {}", sheetName);
                        }
                    }
                }
            }
            return UploadResponse.builder()
                    .notesUploaded(tracker.notesUploadCount)
                    .vocabularyEntriesUploaded(tracker.vocabularyEntriesUploadCount)
                    .tagsUploaded(tracker.tagsUploadCount)
                    .build();
        } catch (IOException e) {
            log.error("Error during upload process", e);
        }
        return UploadResponse.empty();
    }

    // todo: if the settings sheet is uploaded first - the language will not exist
    private void importSettings(XSSFSheet sheet, UploadProgressTracker tracker) {
        validateSettingsHeaderRow(sheet.getRow(HEADER_ROW_NUMBER));
        XSSFRow row = sheet.getRow(1);

        var input = new CreateSettingsInput();
        // todo: dry when safe-accessing cell values
        ofNullable(row)
                .map(r -> r.getCell(XlsxSettingsColumn.DEFAULT_LANGUAGE_NAME.position))
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .ifPresent(input::setDefaultLanguageName);

        ofNullable(row)
                .map(r -> r.getCell(XlsxSettingsColumn.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.position))
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .ifPresent(input::setEntriesPerVocabularyTrainingSession);

        ofNullable(row)
                .map(r -> r.getCell(XlsxSettingsColumn.LANGUAGES_PAGINATION.position))
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .ifPresent(input::setLanguagesPagination);
        ofNullable(row)
                .map(r -> r.getCell(XlsxSettingsColumn.TAGS_PAGINATION.position))
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .ifPresent(input::setTagsPagination);
        ofNullable(row)
                .map(r -> r.getCell(XlsxSettingsColumn.NOTES_PAGINATION.position))
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .ifPresent(input::setNotesPagination);
        ofNullable(row)
                .map(r -> r.getCell(XlsxSettingsColumn.VOCABULARY_PAGINATION.position))
                .map(Cell::getNumericCellValue)
                .map(Double::intValue)
                .ifPresent(input::setVocabularyPagination);

        settingsApi.create(input);

        tracker.settingsUploadFinished();
    }

    private void validateSettingsHeaderRow(XSSFRow row) {
        String validationErrors = collectValidationErrors(
                checkSettingsHeaderColumn(row, XlsxSettingsColumn.DEFAULT_LANGUAGE_NAME),
                checkSettingsHeaderColumn(row, XlsxSettingsColumn.ENTRIES_PER_VOCABULARY_TRAINING_SESSION),
                checkSettingsHeaderColumn(row, XlsxSettingsColumn.LANGUAGES_PAGINATION),
                checkSettingsHeaderColumn(row, XlsxSettingsColumn.TAGS_PAGINATION),
                checkSettingsHeaderColumn(row, XlsxSettingsColumn.NOTES_PAGINATION),
                checkSettingsHeaderColumn(row, XlsxSettingsColumn.VOCABULARY_PAGINATION)
        );
        if (isNotBlank(validationErrors)) {
            throw new XlsxStructureUnsupportedException(validationErrors);
        }
    }

    private Optional<String> checkSettingsHeaderColumn(XSSFRow row, XlsxSettingsColumn column) {
        return collectHeaderValidationError(row, column.position, column.name);
    }

    private void importTags(XSSFSheet sheet, UploadProgressTracker tracker) {
        validateTagsHeaderRow(sheet.getRow(HEADER_ROW_NUMBER));
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            // todo: if the content was empty - track the row and return it in the response
            ofNullable(row)
                    .map(r -> r.getCell(XlsxTagColumn.NAME.position))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(this::isNotBlank)
                    .ifPresent(name -> {
                        tagsApi.create(CreateTagInput.builder()
                                .name(name)
                                .build());
                        tracker.incTagsUploadCount();
                    });
        }
        tracker.tagsUploadFinished();
    }

    private void validateTagsHeaderRow(XSSFRow row) {
        String validationErrors = collectValidationErrors(checkTagsHeaderColumn(row, XlsxTagColumn.NAME));
        if (isNotBlank(validationErrors)) {
            throw new XlsxStructureUnsupportedException(validationErrors);
        }
    }

    @Lookup
    public UploadProgressTracker uploadProgressTracker() {
        return null;
    }

    private void importLanguage(XSSFSheet sheet, UploadProgressTracker tracker) {
        String language = languageApi.findByLanguageName(sheet.getSheetName().substring(XlsxSheetName.LANGUAGE.length()));
        importLanguage(sheet, language, tracker);
    }

    private void importLanguage(XSSFSheet sheet, String language, UploadProgressTracker tracker) {
        validateLanguageHeaderRow(sheet.getRow(HEADER_ROW_NUMBER));
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            ofNullable(row)
                    .map(r -> r.getCell(XlsxVocabularyColumn.WORD.position))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(this::isNotBlank)
                    .ifPresent(vocabularyEntryName -> {
                        String definition = readDefinition(row.getCell(XlsxVocabularyColumn.DEFINITION.position));

                        Set<String> synonyms = getEquivalents(row.getCell(XlsxVocabularyColumn.SYNONYMS.position));

                        int correctAnswersCount = (int) row.getCell(XlsxVocabularyColumn.CORRECT_ANSWERS_COUNT.position).getNumericCellValue();

//                        Set<String> tags = getStringValues(row.getCell(6), ";");
//                        Set<String> contexts = getStringValues(row.getCell(7), "/");

                        Timestamp lastSeenAt = getLastSeenAt(row, XlsxVocabularyColumn.LAST_SEEN_AT.position);

//                        tagService.createIfNotExist(tags);

                        vocabularyEntryApi.create(AddVocabularyEntryInput.builder()
                                .name(vocabularyEntryName)
                                .correctAnswersCount(correctAnswersCount)
                                .definition(definition)
                                .lastSeenAt(lastSeenAt)
                                .language(language)
                                .synonyms(synonyms)
                                .build());
                        tracker.incVocabularyEntriesUploadCount();
                    });
        }
        tracker.vocabularyEntriesUploadFinished();
    }

    private void validateLanguageHeaderRow(XSSFRow headerRow) {
        String validationErrors = collectValidationErrors(
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.WORD),
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.DEFINITION),
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.SYNONYMS),
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.CORRECT_ANSWERS_COUNT),
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.TAGS),
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.CONTEXTS),
                checkLanguageHeaderColumn(headerRow, XlsxVocabularyColumn.LAST_SEEN_AT)
        );
        if (isNotBlank(validationErrors)) {
            throw new XlsxStructureUnsupportedException(validationErrors);
        }
    }

    private Optional<String> checkTagsHeaderColumn(XSSFRow headerRow, XlsxTagColumn column) {
        return collectHeaderValidationError(headerRow, column.position, column.name);
    }

    private Optional<String> checkLanguageHeaderColumn(XSSFRow headerRow, XlsxVocabularyColumn column) {
        return collectHeaderValidationError(headerRow, column.position, column.name);
    }

    private Optional<String> collectHeaderValidationError(XSSFRow headerRow, int position, String name) {
        Optional<String> optionalColumnValue = ofNullable(headerRow.getCell(position))
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank);

        if (optionalColumnValue.isEmpty()) {
            String error = "Expected '" + name + "' header column at index '" + position + "', but was empty" + " (" + headerRow.getSheet().getSheetName() + ")";
            return Optional.of(error);
        }
        String cellValue = optionalColumnValue.get();
        if (!name.equals(cellValue)) {
            String error = "Expected '" + name + "' header column at index '" + position + "', but was '" + cellValue + "' (" + headerRow.getSheet().getSheetName() + ")";
            return Optional.of(error);
        }
        return Optional.empty();
    }

    private void importNotes(XSSFSheet sheet, UploadProgressTracker tracker) {
        validateNoteHeaderRow(sheet.getRow(HEADER_ROW_NUMBER));
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            // todo: if the content was empty - track the row and return it in the response
            ofNullable(row)
                    .map(r -> r.getCell(XlsxNoteColumn.CONTENT.position))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(this::isNotBlank)
                    .ifPresent(content -> {
                        Timestamp lastSeenAt = getLastSeenAt(row, XlsxNoteColumn.LAST_SEEN_AT.position);
                        Set<String> tags = getStringValues(row.getCell(XlsxNoteColumn.TAGS.position), ";");
                        noteApi.add(AddNoteInput.builder()
                                .content(content)
                                .lastSeenAt(lastSeenAt)
                                .tags(tags)
                                .build());
                        tracker.incNotesUploadCount();
                    });
        }
        tracker.notesUploadFinished();
    }

    private Timestamp getLastSeenAt(XSSFRow row, int position) {
        return ofNullable(row.getCell(position))
                .map(XSSFCell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .map(Timestamp::valueOf)
                .orElse(new Timestamp(System.currentTimeMillis()));
    }

    private void validateNoteHeaderRow(XSSFRow headerRow) {
        String validationErrors = collectValidationErrors(
                checkNoteHeaderColumn(headerRow, XlsxNoteColumn.CONTENT),
                checkNoteHeaderColumn(headerRow, XlsxNoteColumn.TAGS),
                checkNoteHeaderColumn(headerRow, XlsxNoteColumn.LAST_SEEN_AT)
        );
        if (isNotBlank(validationErrors)) {
            throw new XlsxStructureUnsupportedException(validationErrors);
        }
    }

    private String collectValidationErrors(Optional<String>... validationError) {
        return Arrays.stream(validationError)
                .flatMap(Optional::stream)
                .collect(joining("\n"));
    }

    private Optional<String> checkNoteHeaderColumn(XSSFRow headerRow, XlsxNoteColumn column) {
        return collectHeaderValidationError(headerRow, column.position, column.name);
    }

    private boolean isNotBlank(String s) {
        return !s.isBlank();
    }

    private Set<String> getStringValues(XSSFCell cell, String separator) {
        return ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .map(s -> Arrays.stream(s.split(separator)))
                .map(s -> s.map(String::strip).filter(this::isNotBlank).collect(toSet()))
                .orElse(Set.of());
    }

    private String readDefinition(XSSFCell cell) {
        return ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .orElse(null);
    }

    private Set<String> getEquivalents(XSSFCell cell) {
        return ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .map(s -> Arrays.stream(s.split(";")))
                .map(s -> s.map(String::strip)
                        .filter(this::isNotBlank)
                        .collect(toSet())
                ).orElse(Collections.emptySet());
    }

}
