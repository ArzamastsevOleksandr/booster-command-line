package cliclient.command.args;

import cliclient.command.Command;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class CommandWithArgs {

    Command command;
    Command helpTarget;

    Long id;
    Long noteId;
    Long vocabularyEntryId;

    String name;
    String languageName;

    String definition;

    String filename;

    VocabularyTrainingSessionMode mode;

    String content;

    Integer correctAnswersCount;

    Integer pagination;
    Integer languagesPagination;
    Integer notesPagination;
    Integer tagsPagination;
    Integer vocabularyPagination;

    String substring;

    String tag;

    Integer entriesPerVocabularyTrainingSession;

    @Builder.Default
    Set<String> addAntonyms = Set.of();
    @Builder.Default
    Set<String> addSynonyms = Set.of();
    @Builder.Default
    Set<String> removeAntonyms = Set.of();
    @Builder.Default
    Set<String> removeSynonyms = Set.of();
    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();
    @Builder.Default
    Set<String> contexts = Set.of();
    @Builder.Default
    Set<String> translations = Set.of();

    @Builder.Default
    List<String> errors = List.of();

}
