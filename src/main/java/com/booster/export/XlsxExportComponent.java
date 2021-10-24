package com.booster.export;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.VocabularyEntryDao;
import com.booster.model.LanguageBeingLearned;
import com.booster.model.VocabularyEntry;
import com.booster.output.CommandLineWriter;
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

    private final CommandLineWriter commandLineWriter;

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final VocabularyEntryDao vocabularyEntryDao;

    public void export(String filename) {
        try (var workbook = new XSSFWorkbook();
             var outputStream = new FileOutputStream(filename)
        ) {
            languageBeingLearnedDao.findAll()
                    .forEach(lbl -> exportLanguageBeingLearned(workbook, lbl));

            workbook.write(outputStream);
        } catch (IOException e) {
            commandLineWriter.writeLine("Error during export process: " + e.getMessage());
        }
    }

    private void exportLanguageBeingLearned(XSSFWorkbook workbook, LanguageBeingLearned languageToExport) {
        XSSFSheet sheet = workbook.createSheet(languageToExport.getLanguageName());

        createHeaderRow(sheet);
        createVocabularyEntryRows(languageToExport, sheet);
    }

    private void createHeaderRow(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);

        row.createCell(0).setCellValue("Word");
        row.createCell(1).setCellValue("Definition");
        row.createCell(2).setCellValue("Synonyms");
        row.createCell(3).setCellValue("Antonyms");
        row.createCell(4).setCellValue("Correct answer count");
        row.createCell(5).setCellValue("Created at");
        row.createCell(6).setCellValue("Vocabulary name");
    }

    private void createVocabularyEntryRows(LanguageBeingLearned languageToExport, XSSFSheet sheet) {
        List<VocabularyEntry> vocabularyEntries = vocabularyEntryDao.findAllForLanguageBeingLearned(languageToExport.getId());

        for (int rowNumber = 0; rowNumber < vocabularyEntries.size(); ++rowNumber) {
            VocabularyEntry vocabularyEntry = vocabularyEntries.get(rowNumber);
            XSSFRow row = sheet.createRow(rowNumber + 1);
            exportVocabularyEntry(vocabularyEntry, row);
        }
    }

    private void exportVocabularyEntry(VocabularyEntry vocabularyEntry, XSSFRow row) {
        row.createCell(0).setCellValue(vocabularyEntry.getName());
        row.createCell(1).setCellValue(vocabularyEntry.getDefinition().orElse(""));
        row.createCell(2).setCellValue(String.join(";", vocabularyEntry.getSynonyms()));
        row.createCell(3).setCellValue(String.join(";", vocabularyEntry.getAntonyms()));
        row.createCell(4).setCellValue(vocabularyEntry.getCorrectAnswersCount());
        row.createCell(5).setCellValue(vocabularyEntry.getCreatedAt().toString());
        row.createCell(6).setCellValue(vocabularyEntry.getVocabularyName());
    }

}
