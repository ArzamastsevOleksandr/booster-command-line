package booster.command.arguments;

import java.util.Optional;

public record ListVocabularyEntriesCommandArgs(Optional<Long> id,
                                               Optional<Integer> pagination,
                                               Optional<String> substring) implements CommandArgs {
}
