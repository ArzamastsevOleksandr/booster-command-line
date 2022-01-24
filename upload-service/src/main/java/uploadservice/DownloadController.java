package uploadservice;

import api.notes.NoteDto;
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
import uploadservice.feign.vocabulary.LanguageControllerApiClient;
import uploadservice.feign.vocabulary.VocabularyEntryControllerApiClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

@Slf4j
@RestController
@RequestMapping(value = "/download/")
@RequiredArgsConstructor
class DownloadController implements DownloadControllerApi {

    private final NotesServiceClient notesServiceClient;
    private final LanguageControllerApiClient languageControllerApiClient;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

    @Override
    public byte[] download() {
        try (var workbook = new XSSFWorkbook()) {
            exportNotes(workbook);
            languageControllerApiClient.getAll().forEach(languageDto -> downloadLanguage(workbook, languageDto));
            try (var out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return out.toByteArray();
            }
        } catch (IOException e) {
            log.error("Error during export process", e);
        }
        throw new RuntimeException();
    }

    private void exportNotes(XSSFWorkbook workbook) {
        List<NoteDto> notes = new ArrayList<>(notesServiceClient.getAll());
        if (!notes.isEmpty()) {
            log.info("Exporting notes");
            XSSFSheet sheet = workbook.createSheet("notes");
            XSSFRow row = sheet.createRow(0);

            // todo: COLUMN_NAMES constants
            row.createCell(0).setCellValue("content");
//            row.createCell(1).setCellValue("Tags");

            for (int i = 0; i < notes.size(); ++i) {
                XSSFRow noteRow = sheet.createRow(i + 1);
                NoteDto note = notes.get(i);
                noteRow.createCell(0).setCellValue(note.content());
//                noteRow.createCell(1).setCellValue(String.join(";", note.getTags()));
            }
        }
    }

    private void downloadLanguage(XSSFWorkbook workbook, LanguageDto languageDto) {
        log.info("Exporting language: {}", languageDto.name());
        XSSFSheet sheet = workbook.createSheet(languageDto.name());

        createLanguageHeaderRow(sheet);
        createVocabularyEntryRows(languageDto, sheet);
    }

    private void createLanguageHeaderRow(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("Word");
        row.createCell(1).setCellValue("Definition");
        row.createCell(2).setCellValue("Synonyms");
        row.createCell(3).setCellValue("Antonyms");
        row.createCell(4).setCellValue("Correct answer count");
        row.createCell(5).setCellValue("Created at");
        row.createCell(6).setCellValue("Tags");
        row.createCell(7).setCellValue("Contexts");
        row.createCell(8).setCellValue("Last seen at");
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
        row.createCell(0).setCellValue(vocabularyEntryDto.getName());
        row.createCell(1).setCellValue(ofNullable(vocabularyEntryDto.getDefinition()).orElse(""));
        row.createCell(2).setCellValue(String.join(";", vocabularyEntryDto.getSynonyms()));
//        row.createCell(3).setCellValue(String.join(";", vocabularyEntryDto.getAntonyms()));
        row.createCell(4).setCellValue(vocabularyEntryDto.getCorrectAnswersCount());
//        row.createCell(5).setCellValue(vocabularyEntryDto.getCreatedAt().toString());
//        row.createCell(6).setCellValue(String.join(";", vocabularyEntryDto.getTags()));
//        row.createCell(7).setCellValue(String.join("/", vocabularyEntryDto.getContexts()));
        row.createCell(8).setCellValue(vocabularyEntryDto.getLastSeenAt().toString());
    }

}
