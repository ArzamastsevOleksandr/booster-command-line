package com.booster.command.arguments;

import com.booster.command.Command;
import com.booster.util.ObjectUtil;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class CommandWithArgs {

    Command command;

    Long id;
    Long languageId;
    Long noteId;
    Long vocabularyEntryId;
    String name;
    String definition;
    String filename;
    TrainingSessionMode mode;
    String content;
    Integer correctAnswersCount;
    Integer pagination;
    String substring;
    String tag;

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
    List<String> errors = List.of();

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
    }

    public Optional<Long> getNoteId() {
        return Optional.ofNullable(noteId);
    }

    public Optional<Long> getVocabularyEntryId() {
        return Optional.ofNullable(vocabularyEntryId);
    }

    public Optional<Long> getLanguageId() {
        return Optional.ofNullable(languageId);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

    public Optional<String> getFilename() {
        return Optional.ofNullable(filename);
    }

    public Optional<String> getTag() {
        return Optional.ofNullable(tag);
    }

    public Optional<TrainingSessionMode> getMode() {
        return Optional.ofNullable(mode);
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<Integer> getCorrectAnswersCount() {
        return Optional.ofNullable(correctAnswersCount);
    }

    public Optional<Integer> getPagination() {
        return Optional.ofNullable(pagination);
    }

    public Optional<String> getSubstring() {
        return Optional.ofNullable(substring);
    }

    public Set<String> getSynonyms() {
        return synonyms == null ? Set.of() : synonyms;
    }

    public Set<String> getAntonyms() {
        return antonyms == null ? Set.of() : antonyms;
    }

    public static CommandWithArgs withErrors(List<String> errors) {
        return CommandWithArgs.builder()
                .errors(ObjectUtil.requireNonNullOrElseThrowIAE(errors, "errors can not be null"))
                .build();
    }

    public static CommandWithArgs singleCommand(Command command) {
        return CommandWithArgs.builder().command(command).build();
    }

    public boolean hasNoErrors() {
        return errors == null || errors.isEmpty();
    }

}
