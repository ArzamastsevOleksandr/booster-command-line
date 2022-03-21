package uploadservice;

import api.notes.NoteDto;
import api.notes.NoteApi;
import api.settings.SettingsDto;
import api.settings.SettingsApi;
import api.tags.TagDto;
import api.tags.TagsApi;
import api.upload.DownloadApi;
import api.vocabulary.LanguageApi;
import api.vocabulary.LanguageDto;
import api.vocabulary.VocabularyEntryApi;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@Slf4j
@RestController
@RequestMapping(value = "/download/")
@RequiredArgsConstructor
class DownloadController implements DownloadApi {

    private static final int HEADER_ROW_NUMBER = 0;

    private final SettingsApi settingsApi;
    private final TagsApi tagsApi;
    private final NoteApi noteApi;
    private final LanguageApi languageControllerApi;
    private final VocabularyEntryApi vocabularyEntryApi;

    @Override
    public byte[] download() {
        try (var workbook = new XSSFWorkbook()) {
            exportTags(workbook);
            exportNotes(workbook);
            languageControllerApi.getAll().forEach(languageDto -> downloadLanguage(workbook, languageDto));
            exportSettings(workbook);
            try (var out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            log.error("Error during export process", e);
        }
        return new byte[]{};
    }

    private void exportSettings(XSSFWorkbook workbook) {
        SettingsDto settingsDto = settingsApi.findOne();

        XSSFSheet sheet = workbook.createSheet(XlsxSheetName.SETTINGS);
        createSettingsHeaderRow(sheet);

        XSSFRow settingsRow = sheet.createRow(1);
        settingsRow
                .createCell(XlsxSettingsColumn.DEFAULT_LANGUAGE_NAME.position)
                .setCellValue(settingsDto.getDefaultLanguageName());
        settingsRow
                .createCell(XlsxSettingsColumn.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.position)
                .setCellValue(settingsDto.getEntriesPerVocabularyTrainingSession());
        settingsRow
                .createCell(XlsxSettingsColumn.LANGUAGES_PAGINATION.position)
                .setCellValue(settingsDto.getLanguagesPagination());
        settingsRow
                .createCell(XlsxSettingsColumn.TAGS_PAGINATION.position)
                .setCellValue(settingsDto.getTagsPagination());
        settingsRow
                .createCell(XlsxSettingsColumn.NOTES_PAGINATION.position)
                .setCellValue(settingsDto.getNotesPagination());
        settingsRow
                .createCell(XlsxSettingsColumn.VOCABULARY_PAGINATION.position)
                .setCellValue(settingsDto.getVocabularyPagination());
    }

    private void createSettingsHeaderRow(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(HEADER_ROW_NUMBER);

        row
                .createCell(XlsxSettingsColumn.DEFAULT_LANGUAGE_NAME.position)
                .setCellValue(XlsxSettingsColumn.DEFAULT_LANGUAGE_NAME.name);
        row
                .createCell(XlsxSettingsColumn.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.position)
                .setCellValue(XlsxSettingsColumn.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.name);
        row
                .createCell(XlsxSettingsColumn.LANGUAGES_PAGINATION.position)
                .setCellValue(XlsxSettingsColumn.LANGUAGES_PAGINATION.name);
        row
                .createCell(XlsxSettingsColumn.TAGS_PAGINATION.position)
                .setCellValue(XlsxSettingsColumn.TAGS_PAGINATION.name);
        row
                .createCell(XlsxSettingsColumn.NOTES_PAGINATION.position)
                .setCellValue(XlsxSettingsColumn.NOTES_PAGINATION.name);
        row
                .createCell(XlsxSettingsColumn.VOCABULARY_PAGINATION.position)
                .setCellValue(XlsxSettingsColumn.VOCABULARY_PAGINATION.name);
    }

    private void exportTags(XSSFWorkbook workbook) {
        List<TagDto> tagDtos = new ArrayList<>(tagsApi.findAll());
        if (!tagDtos.isEmpty()) {
            log.info("Exporting tags");
            XSSFSheet sheet = workbook.createSheet(XlsxSheetName.TAGS);

            createTagsHeaderRow(sheet);
            createTagRows(tagDtos, sheet);
        }
    }

    private void createTagsHeaderRow(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(HEADER_ROW_NUMBER);

        row.createCell(XlsxTagColumn.NAME.position).setCellValue(XlsxTagColumn.NAME.name);
    }

    private void createTagRows(List<TagDto> tagDtos, XSSFSheet sheet) {
        for (int i = 0; i < tagDtos.size(); ++i) {
            XSSFRow tagRow = sheet.createRow(i + 1);
            TagDto tagDto = tagDtos.get(i);
            tagRow.createCell(XlsxTagColumn.NAME.position).setCellValue(tagDto.getName());
        }
    }

    private void exportNotes(XSSFWorkbook workbook) {
        // todo: execute in batches
        List<NoteDto> notes = new ArrayList<>(noteApi.getAll());
        if (!notes.isEmpty()) {
            log.info("Exporting notes");
            XSSFSheet sheet = workbook.createSheet(XlsxSheetName.NOTES);

            createNotesHeaderRow(sheet);
            createNoteRows(notes, sheet);
        }
    }

    private void createNoteRows(List<NoteDto> notes, XSSFSheet sheet) {
        for (int i = 0; i < notes.size(); ++i) {
            XSSFRow noteRow = sheet.createRow(i + 1);
            NoteDto note = notes.get(i);
            noteRow.createCell(XlsxNoteColumn.CONTENT.position).setCellValue(note.getContent());
            noteRow.createCell(XlsxNoteColumn.TAGS.position).setCellValue(note.getTags().stream().map(TagDto::getName).collect(joining(";")));
            noteRow.createCell(XlsxNoteColumn.LAST_SEEN_AT.position).setCellValue(note.getLastSeenAt().toString());
        }
    }

    private void createNotesHeaderRow(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(HEADER_ROW_NUMBER);

        row.createCell(XlsxNoteColumn.CONTENT.position).setCellValue(XlsxNoteColumn.CONTENT.name);
        row.createCell(XlsxNoteColumn.TAGS.position).setCellValue(XlsxNoteColumn.TAGS.name);
        row.createCell(XlsxNoteColumn.LAST_SEEN_AT.position).setCellValue(XlsxNoteColumn.LAST_SEEN_AT.name);
    }

    private void downloadLanguage(XSSFWorkbook workbook, LanguageDto languageDto) {
        log.info("Exporting language: {}", languageDto.name());
        XSSFSheet sheet = workbook.createSheet(XlsxSheetName.LANGUAGE + languageDto.name());

        createLanguageHeaderRow(sheet);
        createVocabularyEntryRows(languageDto, sheet);
    }

    private void createLanguageHeaderRow(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(HEADER_ROW_NUMBER);

        row.createCell(XlsxVocabularyColumn.WORD.position).setCellValue(XlsxVocabularyColumn.WORD.name);
        row.createCell(XlsxVocabularyColumn.DEFINITION.position).setCellValue(XlsxVocabularyColumn.DEFINITION.name);
        row.createCell(XlsxVocabularyColumn.SYNONYMS.position).setCellValue(XlsxVocabularyColumn.SYNONYMS.name);
        row.createCell(XlsxVocabularyColumn.CORRECT_ANSWERS_COUNT.position).setCellValue(XlsxVocabularyColumn.CORRECT_ANSWERS_COUNT.name);
        row.createCell(XlsxVocabularyColumn.TAGS.position).setCellValue(XlsxVocabularyColumn.TAGS.name);
        row.createCell(XlsxVocabularyColumn.CONTEXTS.position).setCellValue(XlsxVocabularyColumn.CONTEXTS.name);
        row.createCell(XlsxVocabularyColumn.LAST_SEEN_AT.position).setCellValue(XlsxVocabularyColumn.LAST_SEEN_AT.name);
    }

    private void createVocabularyEntryRows(LanguageDto languageDto, XSSFSheet sheet) {
        List<VocabularyEntryDto> vocabularyEntries = new ArrayList<>(vocabularyEntryApi.findAllByLanguageId(languageDto.id()));

        for (int rowNumber = 0; rowNumber < vocabularyEntries.size(); ++rowNumber) {
            VocabularyEntryDto vocabularyEntryDto = vocabularyEntries.get(rowNumber);
            XSSFRow row = sheet.createRow(rowNumber + 1);
            exportVocabularyEntry(vocabularyEntryDto, row);
        }
    }

    // todo: export and import must use the same separators (shared component)
    private void exportVocabularyEntry(VocabularyEntryDto vocabularyEntryDto, XSSFRow row) {
        row.createCell(XlsxVocabularyColumn.WORD.position).setCellValue(vocabularyEntryDto.getName());
        row.createCell(XlsxVocabularyColumn.DEFINITION.position).setCellValue(ofNullable(vocabularyEntryDto.getDefinition()).orElse(""));
        row.createCell(XlsxVocabularyColumn.SYNONYMS.position).setCellValue(String.join(";", vocabularyEntryDto.getSynonyms()));
        row.createCell(XlsxVocabularyColumn.CORRECT_ANSWERS_COUNT.position).setCellValue(vocabularyEntryDto.getCorrectAnswersCount());
//        row.createCell(6).setCellValue(String.join(";", vocabularyEntryDto.getTags()));
//        row.createCell(7).setCellValue(String.join("/", vocabularyEntryDto.getContexts()));
        row.createCell(XlsxVocabularyColumn.LAST_SEEN_AT.position).setCellValue(vocabularyEntryDto.getLastSeenAt().toString());
    }

}
