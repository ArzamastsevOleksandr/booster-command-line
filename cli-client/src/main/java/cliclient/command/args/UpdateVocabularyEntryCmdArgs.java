package cliclient.command.args;

import cliclient.command.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVocabularyEntryCmdArgs implements CmdArgs {

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

    @Override
    public Command getCommand() {
        return Command.UPDATE_VOCABULARY_ENTRY;
    }
}
