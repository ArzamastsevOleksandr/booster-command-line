package com.booster.export;

import com.booster.adapter.CommandLineAdapter;
import com.booster.model.Language;
import com.booster.model.Note;
import com.booster.model.VocabularyEntry;
import com.booster.service.LanguageService;
import com.booster.service.NoteService;
import com.booster.service.VocabularyEntryService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class XlsxExportComponent {

    private final CommandLineAdapter adapter;

    private final LanguageService languageService;
    private final VocabularyEntryService vocabularyEntryService;
    private final NoteService noteService;

    public void export(String filename) {
        try (var workbook = new XSSFWorkbook();
             var outputStream = new FileOutputStream(filename)
        ) {
            languageService.findAll().forEach(language -> exportLanguage(workbook, language));

            exportNotes(workbook);

            workbook.write(outputStream);
        } catch (IOException e) {
            adapter.writeLine("Error during export process: " + e.getMessage());
        }
    }

    private void exportNotes(XSSFWorkbook workbook) {
        List<Note> notes = noteService.findAll();
        if (!notes.isEmpty()) {
            adapter.writeLine("Exporting notes");
            XSSFSheet sheet = workbook.createSheet("NOTES");
            XSSFRow row = sheet.createRow(0);

            // todo: COLUMN_NAMES constants
            row.createCell(0).setCellValue("content");
            row.createCell(1).setCellValue("Tags");

            for (int i = 0; i < notes.size(); ++i) {
                XSSFRow noteRow = sheet.createRow(i + 1);
                Note note = notes.get(i);
                noteRow.createCell(0).setCellValue(note.getContent());
                noteRow.createCell(1).setCellValue(String.join(";", note.getTags()));
            }
        }
    }

    private void exportLanguage(XSSFWorkbook workbook, Language language) {
        adapter.writeLine("Exporting language: " + language.getName());
        XSSFSheet sheet = workbook.createSheet(language.getName());

        createLanguageHeaderRow(sheet);
        createVocabularyEntryRows(language, sheet);
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
    }

    private void createVocabularyEntryRows(Language languageToExport, XSSFSheet sheet) {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryService.findAllForLanguageId(languageToExport.getId());

        for (int rowNumber = 0; rowNumber < vocabularyEntries.size(); ++rowNumber) {
            VocabularyEntry vocabularyEntry = vocabularyEntries.get(rowNumber);
            XSSFRow row = sheet.createRow(rowNumber + 1);
            exportVocabularyEntry(vocabularyEntry, row);
        }
    }

    // todo: export and import must use the same separators (shared component)
    private void exportVocabularyEntry(VocabularyEntry vocabularyEntry, XSSFRow row) {
        row.createCell(0).setCellValue(vocabularyEntry.getName());
        row.createCell(1).setCellValue(vocabularyEntry.getDefinition().orElse(""));
        row.createCell(2).setCellValue(String.join(";", vocabularyEntry.getSynonyms()));
        row.createCell(3).setCellValue(String.join(";", vocabularyEntry.getAntonyms()));
        row.createCell(4).setCellValue(vocabularyEntry.getCorrectAnswersCount());
        row.createCell(5).setCellValue(vocabularyEntry.getCreatedAt().toString());
        row.createCell(6).setCellValue(String.join(";", vocabularyEntry.getTags()));
        row.createCell(7).setCellValue(String.join("/", vocabularyEntry.getContexts()));
    }

}
