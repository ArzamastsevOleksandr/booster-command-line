package cliclient.command.arguments;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

@Value
@Builder
public class AddVocabularyEntryCommandArgs implements CommandArgs {

    String name;
    String language;
    String definition;
    String tag;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();
    @Builder.Default
    Set<String> contexts = Set.of();

    public Optional<String> language() {
        return Optional.ofNullable(language);
    }

    public Optional<String> definition() {
        return Optional.ofNullable(definition);
    }

    public Optional<String> tag() {
        return Optional.ofNullable(tag);
    }

}
