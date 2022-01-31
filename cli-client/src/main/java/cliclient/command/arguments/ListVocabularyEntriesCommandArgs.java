package cliclient.command.arguments;

import java.util.Optional;

public record ListVocabularyEntriesCommandArgs(Optional<Long> id,
                                               Integer pagination,
                                               Optional<String> substring) implements CommandArgs {
}
