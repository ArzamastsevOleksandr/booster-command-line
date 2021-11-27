package booster.command.arguments;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

@Value
@Builder
public class AddVocabularyEntryCommandArgs implements CommandArgs {

    String name;
    Long languageId;
    String definition;
    String tag;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();
    @Builder.Default
    Set<String> contexts = Set.of();

    public Optional<Long> languageId() {
        return Optional.ofNullable(languageId);
    }

    public Optional<String> definition() {
        return Optional.ofNullable(definition);
    }

    public Optional<String> tag() {
        return Optional.ofNullable(tag);
    }

}
