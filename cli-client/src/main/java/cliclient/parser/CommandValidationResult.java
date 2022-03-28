package cliclient.parser;

public enum CommandValidationResult {
    NO_INPUT,
    MUST_START_WITH_COMMAND,
    SINGLE_COMMAND,
    HELP_ON_COMMAND,
    HELP_ON_TEXT,
    HELP_EXPECTED_BUT_GOT_OTHER_COMMAND_INSTEAD,
    COMMAND_MUST_BE_FOLLOWED_BY_FLAG_ARGUMENT_PAIRS,
    FORBIDDEN_FLAG_VALUE,
    COMMAND_WITH_FLAG_VALUE_PAIRS
}
