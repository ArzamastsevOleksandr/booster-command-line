package vocabularyservice.vocabularyentry;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vocabularyservice.language.LanguageEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vocabulary_entries")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class VocabularyEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    // todo: do we need an entity, perhaps ID is enough?
    //  Chances are ID is better if we want to minimize the dependencies between packages (we communicate only via services etc).
    @ManyToOne
    private WordEntity word;

    @ManyToOne
    private LanguageEntity language;

    private String definition;
    private int correctAnswersCount;

    @ManyToMany
    private Set<WordEntity> synonyms = new HashSet<>();

}
