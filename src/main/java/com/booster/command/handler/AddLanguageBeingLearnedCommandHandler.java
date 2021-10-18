package com.booster.command.handler;

import com.booster.command.arguments.CommandWithArguments;
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
    public void handle(CommandWithArguments commandWithArguments) {
        List<String> arguments = commandWithArguments.getArguments();

        long languageId = -1;
        for (var arg : arguments) {
            String[] flagAndValue = arg.split("=");
            String flag = flagAndValue[0];
            String value = flagAndValue[1];

            switch (flag) {
                case "id":
                    languageId = Long.parseLong(value);
                    break;
                default:
                    // todo: proper error message
                    System.out.println("ERROR");
            }
        }
        languageBeingLearnedDao.add(languageId);
        commandLineWriter.writeLine("Done.");
        commandLineWriter.newLine();
    }

}
