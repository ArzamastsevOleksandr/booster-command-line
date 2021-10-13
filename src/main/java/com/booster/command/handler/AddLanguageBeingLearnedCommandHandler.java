package com.booster.command.handler;

import com.booster.dao.LanguageBeingLearnedDao;
import com.booster.output.CommandLineWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AddLanguageBeingLearnedCommandHandler {

    private final LanguageBeingLearnedDao languageBeingLearnedDao;

    private final CommandLineWriter commandLineWriter;

    // todo: can add ENGLISH many times (sql fix)
    public void handle(List<String> arguments) {
        String languageId = arguments.get(0);
        languageBeingLearnedDao.add(Long.parseLong(languageId));
        commandLineWriter.writeLine("Done.");
        commandLineWriter.newLine();
    }

}
