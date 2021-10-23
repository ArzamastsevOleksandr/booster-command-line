package com.booster.load;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.dao.VocabularyDao;
import com.booster.dao.VocabularyEntryDao;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.Language;
import com.booster.model.LanguageBeingLearned;
import com.booster.model.Vocabulary;
import com.booster.model.Word;
import com.booster.output.CommandLineWriter;
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
import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class XlsxImportComponent {

    private final CommandLineWriter commandLineWriter;

    private final LanguageBeingLearnedDao languageBeingLearnedDao;
    private final VocabularyDao vocabularyDao;
    private final VocabularyEntryDao vocabularyEntryDao;
    private final WordService wordService;
    private final LanguageService languageService;

    public void load() {
        try (var inputStream = new FileInputStream("import.xlsx");
             var workbook = new XSSFWorkbook(inputStream);
        ) {
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; ++i) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                importSheet(sheet);
            }
        } catch (IOException e) {
            commandLineWriter.writeLine("Error during export process: " + e.getMessage());
        }
    }

    // todo: validation
    private void importSheet(XSSFSheet sheet) {
        String sheetName = sheet.getSheetName();

        languageService.findByName(sheetName)
                .ifPresentOrElse(language -> {
                    importLanguageBeingLearned(sheet, language);
                }, () -> {
                    commandLineWriter.writeLine("No language exists with name: " + sheetName);
                });
    }

    private void importLanguageBeingLearned(XSSFSheet sheet, Language language) {
        final long languageId = language.getId();
        final long languageBeingLearnedId = languageBeingLearnedDao.findByLanguageId(languageId)
                .map(LanguageBeingLearned::getId)
                .orElseGet(() -> languageBeingLearnedDao.add(languageId));

        for (int rowNumber = 1; rowNumber <= sheet.getLastRowNum(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            String vocabularyName = row.getCell(6).getStringCellValue();

            Long vocabularyId = vocabularyDao.findByNameAndLanguageBeingLearnedId(vocabularyName, languageBeingLearnedId)
                    .map(Vocabulary::getId)
                    .orElseGet(() -> vocabularyDao.add(vocabularyName, languageBeingLearnedId));

            String vocabularyEntryName = row.getCell(0).getStringCellValue();
            long wordId = wordService.findByNameOrCreateAndGet(vocabularyEntryName).getId();

            String definition = row.getCell(1).getStringCellValue();

            List<Long> synonymIds = Arrays.stream(row.getCell(2).getStringCellValue().split(";"))
                    .map(wordService::findByNameOrCreateAndGet)
                    .map(Word::getId)
                    .collect(toList());

            List<Long> antonymIds = Arrays.stream(row.getCell(3).getStringCellValue().split(";"))
                    .map(wordService::findByNameOrCreateAndGet)
                    .map(Word::getId)
                    .collect(toList());

            int correctAnswersCount = (int) row.getCell(4).getNumericCellValue();
            Timestamp createdAt = Timestamp.valueOf(row.getCell(5).getStringCellValue());

            var params = AddVocabularyEntryDaoParams.builder()
                    .wordId(wordId)
                    .vocabularyId(vocabularyId)
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
