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
    String name;
    String definition;
    String filename;
    String mode;
    String content;
    Integer correctAnswersCount;
    Integer pagination;
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
    List<String> errors = List.of();

    public Optional<Long> getId() {
        return Optional.ofNullable(id);
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

    public Optional<String> getMode() {
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

    public boolean hasNoErrors() {
        return errors == null || errors.isEmpty();
    }

}
