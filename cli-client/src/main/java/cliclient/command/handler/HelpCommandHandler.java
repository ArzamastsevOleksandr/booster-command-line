package cliclient.command.handler;

import cliclient.adapter.CommandLineAdapter;
import cliclient.command.Command;
import cliclient.command.FlagType;
import cliclient.command.args.CmdArgs;
import cliclient.command.args.HelpCmdArgs;
import cliclient.config.PropertyHolder;
import cliclient.service.ColorProcessor;
import cliclient.util.ColorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final PropertyHolder propertyHolder;
    private final CommandLineAdapter adapter;
    private final ColorProcessor colorProcessor;

    @Override
    public void handle(CmdArgs cwa) {
        var args = (HelpCmdArgs) cwa;
        args.getHelpTarget().ifPresentOrElse(this::helpForTarget, () -> Arrays.stream(Command.values())
                .filter(Command::isRecognizable)
                .map(colorProcessor::coloredCommand)
                .forEach(adapter::writeLine));
    }

    // todo: command().withMandatoryFlags(...).withOptionalFlags(...).withExamples(...)
    private void helpForTarget(Command command) {
        var exampleFileName = "exampleFile.xlsx";
        var tag = "Wisdom";
        String commandDescription = switch (command) {
            case HELP -> """
                    %s:     Help on the global command system
                    %s [c]: Help on a specific command, where [c] is a command
                    Example:
                            %s %s: help on the %s command
                    """.formatted(
                    colorProcessor.coloredCommand(Command.HELP),
                    ColorCodes.green(Command.HELP.firstEquivalent()),
                    ColorCodes.green(Command.HELP.firstEquivalent()),
                    ColorCodes.green(Command.ADD_VOCABULARY_ENTRY.firstEquivalent()),
                    colorProcessor.coloredCommand(Command.ADD_VOCABULARY_ENTRY));
            case LIST_AVAILABLE_LANGUAGES -> """
                    %s: Display available languages
                    """.formatted(colorProcessor.coloredCommand(Command.LIST_AVAILABLE_LANGUAGES));
            case LIST_VOCABULARY_ENTRIES -> """
                    %s: Display vocabulary entries
                    Optional flags:
                    * %s
                    * %s
                    * %s
                    Example:
                            %s \\%s=5 \\%s=ba: will display vocabulary entries in chunks of 5 that contain a 'ba' substring
                            %s \\%s=1: will display a vocabulary entry with id=1
                    """.formatted(
                    colorProcessor.coloredCommand(Command.LIST_VOCABULARY_ENTRIES),
                    ColorCodes.green(FlagType.ID.value),
                    ColorCodes.green(FlagType.SUBSTRING.value),
                    ColorCodes.green(FlagType.PAGINATION.value),
                    ColorCodes.green(Command.LIST_VOCABULARY_ENTRIES.firstEquivalent()),
                    ColorCodes.green(FlagType.PAGINATION.value),
                    ColorCodes.green(FlagType.SUBSTRING.value),
                    ColorCodes.green(Command.LIST_VOCABULARY_ENTRIES.firstEquivalent()),
                    ColorCodes.green(FlagType.ID.value));
            case DELETE_VOCABULARY_ENTRY -> """
                    %s: Delete vocabulary entry
                    Mandatory flags:
                    * %s
                    Example:
                            %s \\%s=1
                    """.formatted(
                    colorProcessor.coloredCommand(Command.DELETE_VOCABULARY_ENTRY),
                    ColorCodes.green(FlagType.ID.value),
                    ColorCodes.green(Command.DELETE_VOCABULARY_ENTRY.firstEquivalent()),
                    ColorCodes.green(FlagType.ID.value));
            case START_VOCABULARY_TRAINING_SESSION -> """
                    %s: Start a vocabulary training session
                    The amount of entries per training session will be calculated as follows:
                        * If there are custom settings in the system, then the %s parameter from the settings will be used
                        * Otherwise, the default value of %s will be used
                    """.formatted(
                    colorProcessor.coloredCommand(Command.START_VOCABULARY_TRAINING_SESSION),
                    ColorCodes.green(FlagType.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.name()),
                    ColorCodes.green(propertyHolder.getEntriesPerVocabularyTrainingSession()));
            case DOWNLOAD -> """
                    %s: Export all the data into the excel file (vocabulary, notes, settings etc)
                    Optional flags:
                    * %s
                    If no filename specified, the default value of '%s' will be used
                    Examples:
                             %s: export the data to the '%s' file
                             %s \\%s=%s: export the data to the %s file
                    """.formatted(
                    colorProcessor.coloredCommand(Command.DOWNLOAD),
                    ColorCodes.green(FlagType.FILE.value),
                    ColorCodes.blue(propertyHolder.getDownloadFilename()),
                    ColorCodes.green(Command.DOWNLOAD.firstEquivalent()),
                    ColorCodes.blue(propertyHolder.getDownloadFilename()),
                    ColorCodes.green(Command.DOWNLOAD.firstEquivalent()),
                    ColorCodes.green(FlagType.FILE.value),
                    ColorCodes.blue(exampleFileName),
                    ColorCodes.blue(exampleFileName));
            case UPLOAD -> """
                    %s: Upload the data from the excel file
                    Optional flags:
                    * %s
                    If no filename specified, the default value of %s will be used
                    Examples:
                             %s: upload data from the %s file
                             %s \\%s=%s: upload data from the %s file
                    """.formatted(
                    colorProcessor.coloredCommand(Command.UPLOAD),
                    ColorCodes.green(FlagType.FILE.value),
                    ColorCodes.blue(propertyHolder.getUploadFilename()),
                    ColorCodes.green(Command.UPLOAD.firstEquivalent()),
                    ColorCodes.blue(propertyHolder.getUploadFilename()),
                    ColorCodes.green(Command.UPLOAD.firstEquivalent()),
                    ColorCodes.green(FlagType.FILE.value),
                    ColorCodes.blue(exampleFileName),
                    ColorCodes.blue(exampleFileName));
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
                    colorProcessor.coloredCommand(Command.LIST_AVAILABLE_LANGUAGES),
                    colorProcessor.coloredCommand(Command.LIST_NOTES),
                    colorProcessor.coloredCommand(Command.LIST_TAGS),
                    colorProcessor.coloredCommand(Command.LIST_VOCABULARY_ENTRIES));
            case ADD_SETTINGS -> """
                    %s: Add custom settings
                    Optional flags:
                    * %s
                    * %s
                    * %s
                    * %s
                    * %s
                    * %s
                    Examples:
                             %s \\%s=ENG \\%s=5: will create settings with default language name ENG and vocabulary training session size of 5
                    """.formatted(
                    colorProcessor.coloredCommand(Command.ADD_SETTINGS),
                    colorProcessor.coloredFlagType(FlagType.LANGUAGE_NAME),
                    colorProcessor.coloredFlagType(FlagType.ENTRIES_PER_VOCABULARY_TRAINING_SESSION),
                    colorProcessor.coloredFlagType(FlagType.LANGUAGES_PAGINATION),
                    colorProcessor.coloredFlagType(FlagType.NOTES_PAGINATION),
                    colorProcessor.coloredFlagType(FlagType.TAGS_PAGINATION),
                    colorProcessor.coloredFlagType(FlagType.VOCABULARY_PAGINATION),
                    ColorCodes.green(Command.ADD_SETTINGS.firstEquivalent()),
                    ColorCodes.green(FlagType.LANGUAGE_NAME.value),
                    ColorCodes.green(FlagType.ENTRIES_PER_VOCABULARY_TRAINING_SESSION.value));
            case DELETE_SETTINGS -> """
                    %s: Delete custom settings
                    """.formatted(colorProcessor.coloredCommand(Command.DELETE_SETTINGS));
            case LIST_NOTES -> """
                    %s: Display notes
                    Optional flags:
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
                    ColorCodes.green(FlagType.PAGINATION.value));
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
                    ColorCodes.green(FlagType.TAG.value));
            case DELETE_NOTE -> """
                    %s: Delete the note
                    Mandatory flags:
                    * %s
                    Example:
                            %s \\%s=1
                    """.formatted(
                    colorProcessor.coloredCommand(Command.DELETE_NOTE),
                    ColorCodes.green(FlagType.ID.value),
                    ColorCodes.green(Command.DELETE_NOTE.firstEquivalent()),
                    ColorCodes.green(FlagType.ID.value));
            case ADD_TAG -> """
                    %s: Create a tag
                    Mandatory flags:
                    * %s
                    Example:
                            %s \\%s=Wisdom
                    """.formatted(
                    colorProcessor.coloredCommand(Command.ADD_TAG),
                    ColorCodes.green(FlagType.NAME.value),
                    ColorCodes.green(Command.ADD_TAG.firstEquivalent()),
                    ColorCodes.green(FlagType.NAME.value));
            case LIST_TAGS -> """
                    %s: Display tags
                    """.formatted(colorProcessor.coloredCommand(Command.LIST_TAGS));
            case USE_TAG -> """
                    %s: Add the tag to the note
                    Mandatory flags:
                    * %s
                    * %s
                    Example:
                            %s \\%s=%s \\%s=12
                    """.formatted(
                    colorProcessor.coloredCommand(Command.USE_TAG),
                    ColorCodes.green(FlagType.TAG.value),
                    ColorCodes.green(FlagType.NOTE_ID.value),
                    ColorCodes.green(Command.USE_TAG.firstEquivalent()),
                    ColorCodes.green(FlagType.TAG.value),
                    tag,
                    ColorCodes.green(FlagType.NOTE_ID.value));
            case LIST_FLAG_TYPES -> """
                    %s: Display supported flags
                    """.formatted(colorProcessor.coloredCommand(Command.LIST_FLAG_TYPES));
            case EXIT -> """
                    %s: Exit
                    Is used to exit the program or the training session
                    """.formatted(colorProcessor.coloredCommand(Command.EXIT));
            default -> throw new RuntimeException("Help not implemented for: " + command);
        };
        adapter.writeLine(commandDescription);
    }

    @Override
    public Command getCommand() {
        return Command.HELP;
    }

}
