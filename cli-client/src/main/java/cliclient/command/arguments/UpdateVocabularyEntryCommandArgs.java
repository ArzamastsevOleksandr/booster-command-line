package cliclient.command.arguments;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

@Value
@Builder
public class UpdateVocabularyEntryCommandArgs implements CommandArgs {

    Long id;
    String name;
    String definition;
    Integer correctAnswersCount;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();
    @Builder.Default
    Set<String> addAntonyms = Set.of();
    @Builder.Default
    Set<String> addSynonyms = Set.of();
    @Builder.Default
    Set<String> removeAntonyms = Set.of();
    @Builder.Default
    Set<String> removeSynonyms = Set.of();

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

    public Optional<Integer> getCorrectAnswersCount() {
        return Optional.ofNullable(correctAnswersCount);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

}
