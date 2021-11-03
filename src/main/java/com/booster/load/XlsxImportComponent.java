package com.booster.load;

import com.booster.adapter.CommandLineAdapter;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.Language;
import com.booster.model.Word;
import com.booster.service.LanguageService;
import com.booster.service.WordService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class XlsxImportComponent {

    private final CommandLineAdapter adapter;

    private final VocabularyEntryDao vocabularyEntryDao;
    private final WordService wordService;
    private final LanguageService languageService;

    public void load(String filename) {
        try (var inputStream = new FileInputStream(filename);
             var workbook = new XSSFWorkbook(inputStream);
        ) {
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; ++i) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                importSheet(sheet);
            }
        } catch (IOException e) {
            adapter.writeLine("Error during export process: " + e.getMessage());
        }
    }

    // todo: validation
    private void importSheet(XSSFSheet sheet) {
        String sheetName = sheet.getSheetName();

        languageService.findByName(sheetName)
                .ifPresentOrElse(language -> {
                    importLanguage(sheet, language);
                }, () -> {
                    adapter.writeLine("No language exists with name: " + sheetName);
                });
    }

    private void importLanguage(XSSFSheet sheet, Language language) {
        final long languageId = language.getId();

        for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            String vocabularyEntryName = row.getCell(0).getStringCellValue();
            long wordId = wordService.findByNameOrCreateAndGet(vocabularyEntryName).getId();

            String definition = row.getCell(1).getStringCellValue();

            Set<Long> synonymIds = Arrays.stream(row.getCell(2).getStringCellValue().split(";"))
                    .map(wordService::findByNameOrCreateAndGet)
                    .map(Word::getId)
                    .collect(toSet());

            Set<Long> antonymIds = Arrays.stream(row.getCell(3).getStringCellValue().split(";"))
                    .map(wordService::findByNameOrCreateAndGet)
                    .map(Word::getId)
                    .collect(toSet());

            int correctAnswersCount = (int) row.getCell(4).getNumericCellValue();
            Timestamp createdAt = Timestamp.valueOf(row.getCell(5).getStringCellValue());

            var params = AddVocabularyEntryDaoParams.builder()
                    .wordId(wordId)
                    .languageId(languageId)
                    .synonymIds(synonymIds)
                    .antonymIds(antonymIds)
                    .correctAnswersCount(correctAnswersCount)
                    .createdAt(createdAt)
                    .definition(definition)
                    .build();
            vocabularyEntryDao.addWithAllValues(params);
        }
    }

}
