package uploadservice;

import api.notes.NoteDto;
import api.tags.TagDto;
import api.upload.DownloadControllerApi;
import api.vocabulary.LanguageDto;
import api.vocabulary.VocabularyEntryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uploadservice.feign.notes.NotesServiceClient;
import uploadservice.feign.tags.TagServiceClient;
import uploadservice.feign.vocabulary.LanguageControllerApiClient;
import uploadservice.feign.vocabulary.VocabularyEntryControllerApiClient;

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
class DownloadController implements DownloadControllerApi {

    private static final int HEADER_ROW_NUMBER = 0;

    private final TagServiceClient tagServiceClient;
    private final NotesServiceClient notesServiceClient;
    private final LanguageControllerApiClient languageControllerApiClient;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    @Override
    public byte[] download() {
        try (var workbook = new XSSFWorkbook()) {
            exportTags(workbook);
            exportNotes(workbook);
            languageControllerApiClient.getAll().forEach(languageDto -> downloadLanguage(workbook, languageDto));
            try (var out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            log.error("Error during export process", e);
        }
        return new byte[]{};
    }

    private void exportTags(XSSFWorkbook workbook) {
        List<TagDto> tagDtos = new ArrayList<>(tagServiceClient.findAll());
        if (!tagDtos.isEmpty()) {
            log.info("Exporting tags");
            XSSFSheet sheet = workbook.createSheet("tags");

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
        List<NoteDto> notes = new ArrayList<>(notesServiceClient.getAll());
        if (!notes.isEmpty()) {
            log.info("Exporting notes");
            XSSFSheet sheet = workbook.createSheet("notes");

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
        XSSFSheet sheet = workbook.createSheet(languageDto.name());

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
        List<VocabularyEntryDto> vocabularyEntries = new ArrayList<>(vocabularyEntryControllerApiClient.findAllByLanguageId(languageDto.id()));

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
