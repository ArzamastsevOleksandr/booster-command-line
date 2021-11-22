package com.booster.load;

import com.booster.adapter.CommandLineAdapter;
import com.booster.dao.params.AddNoteDaoParams;
import com.booster.dao.params.AddVocabularyEntryDaoParams;
import com.booster.model.Language;
import com.booster.model.Word;
import com.booster.service.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
@RequiredArgsConstructor
public class XlsxImportComponent {

    private final CommandLineAdapter adapter;

    private final VocabularyEntryService vocabularyEntryService;
    private final WordService wordService;
    private final LanguageService languageService;
    private final NoteService noteService;
    private final TagService tagService;

    public void load(String filename) {
        try (var inputStream = new FileInputStream(filename);
             var workbook = new XSSFWorkbook(inputStream);
        ) {
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; ++i) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().strip().equalsIgnoreCase("NOTES")) {
                    importNotes(sheet);
                } else {
                    importLanguages(sheet);
                }
            }
        } catch (IOException e) {
            adapter.writeLine("Error during export process: " + e.getMessage());
        }
    }

    private void importNotes(XSSFSheet sheet) {
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            Optional.ofNullable(row)
                    .map(r -> r.getCell(0))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(s -> !s.isBlank())
                    .ifPresent(content -> {
                        Set<String> tags = getStringValues(row.getCell(1), ";");
                        tagService.createIfNotExist(tags);
                        noteService.add(AddNoteDaoParams.builder().content(content).tags(tags).build());
                    });
        }
    }

    // todo: validation
    // todo: reliable way to get number of rows in xlsx
    private void importLanguages(XSSFSheet sheet) {
        String sheetName = sheet.getSheetName();

        languageService.findByName(sheetName)
                .map(Language::getId)
                .ifPresentOrElse(languageId -> {
                    importLanguage(sheet, languageId);
                }, () -> {
                    adapter.writeLine("Creating language with name: " + sheetName);
                    Language language = languageService.add(sheetName);
                    importLanguage(sheet, language.getId());
                });
    }

    private void importLanguage(XSSFSheet sheet, long languageId) {
        for (int rowNumber = 1; rowNumber <= sheet.getPhysicalNumberOfRows(); ++rowNumber) {
            XSSFRow row = sheet.getRow(rowNumber);

            Optional.ofNullable(row)
                    .map(r -> r.getCell(0))
                    .map(Cell::getStringCellValue)
                    .map(String::strip)
                    .filter(s -> !s.isBlank())
                    .ifPresent(vocabularyEntryName -> {
                        long wordId = wordService.findByNameOrCreateAndGet(vocabularyEntryName).getId();

                        String definition = readDefinition(row.getCell(1));

                        Set<Long> synonymIds = getEquivalentIds(row.getCell(2));
                        Set<Long> antonymIds = getEquivalentIds(row.getCell(3));

                        int correctAnswersCount = (int) row.getCell(4).getNumericCellValue();
                        Timestamp createdAt = Timestamp.valueOf(row.getCell(5).getStringCellValue());

                        Set<String> tags = getStringValues(row.getCell(6), ";");
                        Set<String> contexts = getStringValues(row.getCell(7), "/");
                        tagService.createIfNotExist(tags);

                        var params = AddVocabularyEntryDaoParams.builder()
                                .wordId(wordId)
                                .languageId(languageId)
                                .synonymIds(synonymIds)
                                .antonymIds(antonymIds)
                                .correctAnswersCount(correctAnswersCount)
                                .createdAt(createdAt)
                                .definition(definition)
                                .tags(tags)
                                .contexts(contexts)
                                .build();
                        vocabularyEntryService.addWithAllValues(params);
                    });
        }
    }

    private Set<String> getStringValues(XSSFCell cell, String separator) {
        return Optional.ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .map(s -> Arrays.stream(s.split(separator)))
                .map(s -> s.map(String::strip).filter(str -> !str.isBlank()).collect(toSet()))
                .orElse(Set.of());
    }

    private String readDefinition(XSSFCell cell) {
        return Optional.ofNullable(cell)
                .map(Cell::getStringCellValue)
                .orElse(null);
    }

    private Set<Long> getEquivalentIds(XSSFCell cell) {
        return Optional.ofNullable(cell)
                .map(Cell::getStringCellValue)
                .map(String::strip)
                .filter(s -> !s.isBlank())
                .map(s -> Arrays.stream(s.split(";")))
                .map(s -> s.map(String::strip)
                        .filter(str -> !str.isBlank())
                        .map(wordService::findByNameOrCreateAndGet)
                        .map(Word::getId)
                        .collect(toSet())
                ).orElse(Collections.emptySet());
    }

}
