package vocabularyservice.vocabularyentry;

import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @ManyToOne
    private WordEntity wordEntity;

    private String definition;
    private int correctAnswersCount;

    @ManyToMany
    private Set<WordEntity> synonyms = new HashSet<>();

}
