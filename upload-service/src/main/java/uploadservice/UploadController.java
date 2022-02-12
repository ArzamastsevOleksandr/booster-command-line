package uploadservice;

import api.exception.XlsxStructureUnsupportedException;
import api.notes.AddNoteInput;
import api.upload.UploadControllerApi;
import api.upload.UploadResponse;
import api.vocabulary.AddLanguageInput;
import api.vocabulary.AddVocabularyEntryInput;
import api.vocabulary.LanguageDto;
import feign.FeignException;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

@Slf4j
@RestController
@RequestMapping(value = "/upload/")
@RequiredArgsConstructor
class UploadController implements UploadControllerApi {

    private static final int HEADER_ROW_NUMBER = 0;

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
                    case "english" -> importLanguage(sheet, tracker);
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

    private void importLanguage(XSSFSheet sheet, UploadProgressTracker tracker) {
        LanguageDto languageDto = findLanguageByNameOrCreateIfNotExists(sheet.getSheetName());
        importLanguage(sheet, languageDto.id(), tracker);
    }

    private LanguageDto findLanguageByNameOrCreateIfNotExists(String sheetName) {
        try {
            return languageControllerApiClient.findByName(sheetName);
        } catch (FeignException.FeignClientException e) {
            if (e.status() == 404) {
                return languageControllerApiClient.add(new AddLanguageInput(sheetName));
            } else {
                throw e;
            }
        }
    }

    private void importLanguage(XSSFSheet sheet, Long id, UploadProgressTracker tracker) {
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

                        Timestamp lastSeenAt = ofNullable(row.getCell(XlsxVocabularyColumn.LAST_SEEN_AT.position))
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
                                .lastSeenAt(lastSeenAt)
                                .languageId(id)
                                .synonyms(synonyms)
                                .build());
                        tracker.incVocabularyEntriesUploadCount();
                    });
        }
        tracker.vocabularyEntriesUploadFinished();
    }

    private void importNotes(XSSFSheet sheet, UploadProgressTracker tracker) {
        validateNoteHeaderRow(sheet);
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            ofNullable(row)
                    .map(r -> r.getCell(XlsxNoteColumn.CONTENT.position))
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

    private void validateNoteHeaderRow(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.getRow(HEADER_ROW_NUMBER);

        Optional<String> contentValidationError = collectNoteHeaderValidationError(headerRow, XlsxNoteColumn.CONTENT);
        Optional<String> tagsValidationError = collectNoteHeaderValidationError(headerRow, XlsxNoteColumn.TAGS);

        String validationErrors = Stream.concat(contentValidationError.stream(), tagsValidationError.stream())
                .collect(Collectors.joining("\n"));

        if (isNotBlank(validationErrors)) {
            throw new XlsxStructureUnsupportedException(validationErrors);
        }
    }

    private Optional<String> collectNoteHeaderValidationError(XSSFRow headerRow, XlsxNoteColumn column) {
        Optional<String> optionalColumnValue = ofNullable(headerRow.getCell(column.position))
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(this::isNotBlank);

        if (optionalColumnValue.isEmpty()) {
            String error = "Expected '" + column.name + "' header column at index '" + column.position + "', but was empty" + " (" + headerRow.getSheet().getSheetName() + ")";
            return Optional.of(error);
        }
        String cellValue = optionalColumnValue.get();
        if (!column.name.equals(cellValue)) {
            String error = "Expected '" + column.name + "' header column at index '" + column.position + "', but was " + cellValue + " (" + headerRow.getSheet().getSheetName() + ")";
            return Optional.of(error);
        }
        return Optional.empty();
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
