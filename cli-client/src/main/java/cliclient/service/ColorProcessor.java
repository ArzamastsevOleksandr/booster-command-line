package cliclient.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ColorProcessor {

    @Value("${vocabulary.entry.color.enabled:true}")
    private boolean vecEnabled;

//    public String coloredEntry(VocabularyEntry entry) {
//        if (vecEnabled) {
//            var builder = new StringBuilder();
//            builder.append(entry.getClass().getSimpleName())
//                    .append("(id=").append(green(entry.getId()))
//                    .append(", name=").append(green(entry.getName()))
//                    .append(", cac=").append(green(entry.getCorrectAnswersCount()))
//                    .append(", language=").append(yellow(entry.getLanguageName()))
//                    .append(", lid=").append(yellow(entry.getLanguageId()))
//                    .append(", createdAt=").append(purple(entry.getCreatedAt()))
//                    .append(", lastSeenAt=").append(purple(entry.getLastSeenAt())).append(")\n");
//            entry.getDefinition().ifPresent(definition -> {
//                builder.append("  --definition=").append(green(definition)).append("\n");
//            });
//            if (!entry.getSynonyms().isEmpty()) {
//                builder.append("  --synonyms=").append(cyan(join(", ", entry.getSynonyms()))).append('\n');
//            }
//            if (!entry.getAntonyms().isEmpty()) {
//                builder.append("  --antonyms=").append(red(join(", ", entry.getAntonyms()))).append('\n');
//            }
//            if (!entry.getTags().isEmpty()) {
//                builder.append("  --tags=").append(yellow(join(", ", entry.getTags()))).append('\n');
//            }
//            if (!entry.getContexts().isEmpty()) {
//                builder.append("  --contexts=").append(blue(join(", ", entry.getContexts()))).append('\n');
//            }
//            return builder.toString();
//        }
//        return entry.toString();
//    }

}
