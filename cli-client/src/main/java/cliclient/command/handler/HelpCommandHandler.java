package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.arguments.CommandArgs;
import cliclient.command.arguments.HelpCommandArgs;
import cliclient.service.ColorProcessor;
import cliclient.util.ColorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;

    @Value("${session.vocabulary.size:10}")
    private int entriesPerSession;
    @Value("${upload.filename:upload.xlsx}")
    private String uploadFilename;
    @Value("${download.filename:download.xlsx}")
    private String downloadFilename;

    @Override
    public void handle(CommandArgs commandArgs) {
        var args = (HelpCommandArgs) commandArgs;
        args.getHelpTarget().ifPresentOrElse(this::helpForTarget, () -> Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .map(colorProcessor::coloredCommand)
                .forEach(adapter::writeLine));
    }

    private void helpForTarget(Command command) {
        var exampleFileName = "exampleFile.xlsx";
        String commandDescription = switch (command) {
            case HELP -> """
                    %s:     Help on the global command system
                    %s [c]: Help on a specific command usage, where [c] is a command
                    Example:
                            %s %s:  get help on the %s command
                    """.formatted(
                    colorProcessor.coloredCommand(Command.HELP),
                    ColorCodes.green(Command.HELP.firstEquivalent()),
                    ColorCodes.green(Command.HELP.firstEquivalent()),
                    ColorCodes.green(Command.ADD_VOCABULARY_ENTRY.firstEquivalent()),
                    colorProcessor.coloredCommand(Command.ADD_VOCABULARY_ENTRY)
            );
            case LIST_LANGUAGES -> """
                    %s: List all languages being learned
                    """.formatted(colorProcessor.coloredCommand(Command.LIST_LANGUAGES));
            case ADD_LANGUAGE -> """
                    %s: Add a new language to be learned
                    Example:
                            %s \\%s=English
                    """.formatted(
                    colorProcessor.coloredCommand(Command.ADD_LANGUAGE),
                    ColorCodes.green(Command.ADD_LANGUAGE.firstEquivalent()),
                    ColorCodes.green(FlagType.NAME.value)
            );
            case DELETE_LANGUAGE -> """
                    %s: Delete a language
                    Example:
                            %s \\%s=123
                    """.formatted(
                    colorProcessor.coloredCommand(Command.DELETE_LANGUAGE),
                    ColorCodes.green(Command.DELETE_LANGUAGE.firstEquivalent()),
                    ColorCodes.green(FlagType.ID.value)
            );
            case START_VOCABULARY_TRAINING_SESSION -> """
                    %s: Start a vocabulary training session
                    The amount of entries per training session will be calculated as follows:
                        * If there are custom settings in the system, then the %s parameter from the settings will be used
                        * Otherwise, the default of %s entries will be used
                    """.formatted(
                    colorProcessor.coloredCommand(Command.START_VOCABULARY_TRAINING_SESSION),
                    ColorCodes.green(FlagType.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.name()),
                    ColorCodes.green(entriesPerSession)
            );
            case DOWNLOAD -> """
                    %s: Export all the data into the excel file (vocabulary, notes, settings etc)
                        * If no filename specified, the default value of '%s' will be used
                        * Otherwise, the specified filename will be used
                        Examples:
                                 %s: export the data to the '%s' file
                                 %s \\f=%s: export the data to the %s file
                    """.formatted(
                    colorProcessor.coloredCommand(Command.DOWNLOAD),
                    ColorCodes.green(downloadFilename),
                    ColorCodes.green(Command.DOWNLOAD.firstEquivalent()),
                    ColorCodes.green(downloadFilename),
                    ColorCodes.green(Command.DOWNLOAD.firstEquivalent()),
                    ColorCodes.green(exampleFileName),
                    ColorCodes.green(exampleFileName)
            );
            case UPLOAD -> """
                    %s: Upload the data from the custom excel file
                        * If no filename specified, the default value of %s will be used
                        * Otherwise, the specified filename will be used
                        Examples:
                                 %s: upload the data from the %s file
                                 %s \\f=%s: upload the data from the %s file
                    """.formatted(
                    colorProcessor.coloredCommand(Command.UPLOAD),
                    ColorCodes.green(uploadFilename),
                    ColorCodes.green(Command.UPLOAD.firstEquivalent()),
                    ColorCodes.green(uploadFilename),
                    ColorCodes.green(Command.UPLOAD.firstEquivalent()),
                    ColorCodes.green(exampleFileName),
                    ColorCodes.green(exampleFileName)
            );
            case SHOW_SETTINGS -> """
                    %s: Show the custom settings
                        The settings may contain the following configurations:
                        
                        * default language id
                        * default language name (is directly related to the default language id)
                        
                        * number of words per vocabulary training session
                        
                        * pagination parameter for the %s command
                        * pagination parameter for the %s command
                        * pagination parameter for the %s command
                        * pagination parameter for the %s command
                    """.formatted(
                    colorProcessor.coloredCommand(Command.SHOW_SETTINGS),
                    colorProcessor.coloredCommand(Command.LIST_LANGUAGES),
                    colorProcessor.coloredCommand(Command.LIST_NOTES),
                    colorProcessor.coloredCommand(Command.LIST_TAGS),
                    colorProcessor.coloredCommand(Command.LIST_VOCABULARY_ENTRIES)
            );
            case ADD_SETTINGS -> """
                    %s: Add custom settings
                    The following optional flags may be used with this command:
                    * %s
                    * %s
                    * %s
                    * %s
                    * %s
                    * %s
                    * %s
                    Examples:
                             %s \\%s=1 \\%s=5: will create settings with default language id 1 and vocabulary training session size of 5
                    """.formatted(
                    colorProcessor.coloredCommand(Command.ADD_SETTINGS),
                    colorProcessor.coloredFlagType(FlagType.LANGUAGE_ID),
                    colorProcessor.coloredFlagType(FlagType.LANGUAGE_NAME),
                    colorProcessor.coloredFlagType(FlagType.ENTRIES_PER_VOCABULARY_TRAINING_SESSION),
                    colorProcessor.coloredFlagType(FlagType.LANGUAGES_PAGINATION),
                    colorProcessor.coloredFlagType(FlagType.NOTES_PAGINATION),
                    colorProcessor.coloredFlagType(FlagType.TAGS_PAGINATION),
                    colorProcessor.coloredFlagType(FlagType.VOCABULARY_PAGINATION),
                    ColorCodes.green(Command.ADD_SETTINGS.firstEquivalent()),
                    ColorCodes.green(FlagType.LANGUAGE_ID.value),
                    ColorCodes.green(FlagType.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.value)
            );
            case DELETE_SETTINGS -> """
                    %s: Delete custom settings
                    """.formatted(colorProcessor.coloredCommand(Command.DELETE_SETTINGS));
            case LIST_NOTES -> """
                    %s: Display notes
                    The following optional flags may be used with this command:
                    * %s
                    * %s
                    Examples:
                            %s \\%s=1: will display the note by id 1
                            %s \\%s=5: will display notes in chunks of 5
                    """.formatted(
                    colorProcessor.coloredCommand(Command.LIST_NOTES),
                    colorProcessor.coloredFlagType(FlagType.ID),
                    colorProcessor.coloredFlagType(FlagType.PAGINATION),
                    ColorCodes.green(Command.LIST_NOTES.firstEquivalent()),
                    ColorCodes.green(FlagType.ID.value),
                    ColorCodes.green(Command.LIST_NOTES.firstEquivalent()),
                    ColorCodes.green(FlagType.PAGINATION.value)
            );
            case ADD_NOTE -> """
                    %s: Create a note
                    Mandatory flags:
                    * %s
                    Optional flags:
                    * %s
                    Example:
                            %s \\%s=Slowly is the fastest way \\%s=wisdom
                    """.formatted(
                    colorProcessor.coloredCommand(Command.ADD_NOTE),
                    ColorCodes.green(FlagType.CONTENT.value),
                    ColorCodes.green(FlagType.TAG.value),
                    ColorCodes.green(Command.ADD_NOTE.firstEquivalent()),
                    ColorCodes.green(FlagType.CONTENT.value),
                    ColorCodes.green(FlagType.TAG.value)
            );
            case DELETE_NOTE -> """
                    %s: Delete the note by id
                    Mandatory flags:
                    * %s
                    Example:
                            %s \\%s=1
                    """.formatted(
                    colorProcessor.coloredCommand(Command.DELETE_NOTE),
                    ColorCodes.green(FlagType.ID.value),
                    ColorCodes.green(Command.DELETE_NOTE.firstEquivalent()),
                    ColorCodes.green(FlagType.ID.value)
            );
            default -> throw new RuntimeException("Help not implemented for: " + command);
        };
        adapter.writeLine(commandDescription);
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
