package cliclient.model;

import lombok.Builder;
import lombok.Value;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class VocabularyEntry {

    long id;
    String name;
    long wordId;
    Timestamp createdAt;
    Timestamp lastSeenAt;
    int correctAnswersCount;

    // todo: custom ds
    Long languageId;
    String languageName;

    String definition;

    @Builder.Default
    Set<String> synonyms = Set.of();
    @Builder.Default
    Set<String> antonyms = Set.of();
    @Builder.Default
    Set<String> contexts = Set.of();
    @Builder.Default
    Set<String> tags = Set.of();

    public Optional<String> getDefinition() {
        return Optional.ofNullable(definition);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName())
                .append("(id=").append(id)
                .append(", name=").append(name)
                .append(", cac=").append(correctAnswersCount)
                .append(", language=").append(languageName)
                .append(", lid=").append(languageId)
                .append(", createdAt=").append(createdAt)
                .append(", lastSeenAt=").append(lastSeenAt).append(")\n");
        if (definition != null) {
            builder.append("  --definition=").append(definition).append("\n");
        }
        if (!synonyms.isEmpty()) {
            builder.append("  --synonyms=").append(String.join(",", synonyms)).append('\n');
        }
        if (!antonyms.isEmpty()) {
            builder.append("  --antonyms=").append(String.join(",", antonyms)).append('\n');
        }
        if (!tags.isEmpty()) {
            builder.append("  --tags=").append(String.join(",", tags)).append('\n');
        }
        if (!contexts.isEmpty()) {
            builder.append("  --contexts=").append(String.join(",", contexts)).append('\n');
        }
        return builder.toString();
    }

}
