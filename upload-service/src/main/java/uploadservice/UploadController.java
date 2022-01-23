package uploadservice;

import api.notes.AddNoteInput;
import api.upload.UploadControllerApi;
import api.upload.UploadResponse;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Slf4j
@RestController
@RequestMapping(value = "/upload/")
@RequiredArgsConstructor
class UploadController implements UploadControllerApi {

    private final NotesServiceClient notesServiceClient;

    @Override
    public UploadResponse upload(MultipartFile file) {
        try (var inputStream = file.getInputStream();
             var workbook = new XSSFWorkbook(inputStream)) {

            int numberOfSheets = workbook.getNumberOfSheets();

            var tracker = importProgressTracker();
            for (int i = 0; i < numberOfSheets; ++i) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName().strip().toLowerCase();

                switch (sheetName) {
                    case "notes" -> importNotes(sheet, tracker);
                    case "language" -> importLanguages(sheet, tracker);
                    default -> log.error("Unrecognized upload page ignored: {}", sheetName);
                }
            }
            return new UploadResponse(tracker.vocabularyEntriesImportCount, tracker.notesImportCount);
        } catch (IOException e) {
            log.error("Error during import process", e);
        }
        return new UploadResponse(0, 0);
    }

    @Lookup
    public ImportProgressTracker importProgressTracker() {
        return null;
    }

    private void importLanguages(XSSFSheet sheet, ImportProgressTracker importProgressTracker) {
        log.info("language NOT IMPL");
    }

    private void importNotes(XSSFSheet sheet, ImportProgressTracker importProgressTracker) {
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
                        importProgressTracker.incNotesImportCount();
                    });
        }
        importProgressTracker.notesImportFinished();
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

}
