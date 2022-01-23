package uploadservice;

import api.notes.AddNoteInput;
import api.upload.UploadControllerApi;
import api.upload.UploadResponse;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.LanguageDto;
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
import uploadservice.feign.notes.NotesServiceClient;
import uploadservice.feign.vocabulary.LanguageControllerApiClient;
import uploadservice.feign.vocabulary.VocabularyEntryControllerApiClient;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
@RestController
@RequestMapping(value = "/upload/")
@RequiredArgsConstructor
class UploadController implements UploadControllerApi {

    private final NotesServiceClient notesServiceClient;
    private final LanguageControllerApiClient languageControllerApiClient;
    private final VocabularyEntryControllerApiClient vocabularyEntryControllerApiClient;

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
                    case "notes" -> importNotes(sheet, tracker);
                    // todo: language recognition pattern (no hardcoding)
                    case "english" -> importLanguages(sheet, tracker);
                    default -> log.error("Unrecognized upload page ignored: {}", sheetName);
                }
            }
            return new UploadResponse(tracker.vocabularyEntriesUploadCount, tracker.notesUploadCount);
        } catch (IOException e) {
            log.error("Error during upload process", e);
        }
        return new UploadResponse(0, 0);
    }

    @Lookup
    public UploadProgressTracker uploadProgressTracker() {
        return null;
    }

    private void importLanguages(XSSFSheet sheet, UploadProgressTracker tracker) {
        String sheetName = sheet.getSheetName();

        LanguageDto languageDto = languageControllerApiClient.findByName(sheetName);
        importLanguage(sheet, languageDto.id(), tracker);
        // if lang does not exist: create one
    }

    private void importLanguage(XSSFSheet sheet, Long id, UploadProgressTracker tracker) {
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            Optional.ofNullable(row)
                    .map(r -> r.getCell(0))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(this::isNotBlank)
                    .ifPresent(vocabularyEntryName -> {
                        String definition = readDefinition(row.getCell(1));

                        Set<String> synonyms = getEquivalents(row.getCell(2));

                        int correctAnswersCount = (int) row.getCell(4).getNumericCellValue();
                        var createdAt = Timestamp.valueOf(row.getCell(5).getStringCellValue());

//                        Set<String> tags = getStringValues(row.getCell(6), ";");
//                        Set<String> contexts = getStringValues(row.getCell(7), "/");

                        // todo: column index is not a magic number
                        Timestamp lastSeenAt = Optional.ofNullable(row.getCell(8))
                                .map(XSSFCell::getStringCellValue)
                                .map(String::strip)
                                .filter(this::isNotBlank)
                                .map(Timestamp::valueOf)
                                .orElse(new Timestamp(System.currentTimeMillis()));

//                        tagService.createIfNotExist(tags);

                        vocabularyEntryControllerApiClient.add(AddVocabularyEntryInput.builder()
                                .name(vocabularyEntryName)
                                .correctAnswersCount(correctAnswersCount)
                                .definition(definition)
                                .languageId(id)
                                .synonyms(synonyms)
                                .build());
                        tracker.incVocabularyEntriesUploadCount();
                    });
        }
        tracker.vocabularyEntriesUploadFinished();
    }

    private void importNotes(XSSFSheet sheet, UploadProgressTracker tracker) {
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            Optional.ofNullable(row)
                    .map(r -> r.getCell(0))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(this::isNotBlank)
                    .ifPresent(content -> {
//                        Set<String> tags = getStringValues(row.getCell(1), ";");
//                        tagService.createIfNotExist(tags);
                        notesServiceClient.add(new AddNoteInput(content));
                        tracker.incNotesUploadCount();
                    });
        }
        tracker.notesUploadFinished();
    }

    private boolean isNotBlank(String s) {
        return !s.isBlank();
    }

    private Set<String> getStringValues(XSSFCell cell, String separator) {
        return Optional.ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .map(s -> Arrays.stream(s.split(separator)))
                .map(s -> s.map(String::strip).filter(this::isNotBlank).collect(toSet()))
                .orElse(Set.of());
    }

    private String readDefinition(XSSFCell cell) {
        return Optional.ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank)
                .orElse(null);
    }

    private Set<String> getEquivalents(XSSFCell cell) {
        return Optional.ofNullable(cell)
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
