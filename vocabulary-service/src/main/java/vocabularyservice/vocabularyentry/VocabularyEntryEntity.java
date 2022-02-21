package vocabularyservice.vocabularyentry;

import lombok.*;
import org.hibernate.Hibernate;
import vocabularyservice.language.LanguageEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vocabulary_entries")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
class VocabularyEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    private Timestamp lastSeenAt = new Timestamp(System.currentTimeMillis());

    // todo: do we need an entity, perhaps ID is enough?
    //  Chances are ID is better if we want to minimize the dependencies between packages (we communicate only via services etc).
    @ManyToOne
    private WordEntity word;

    @ManyToOne
    private LanguageEntity language;

    private String definition;
    private Integer correctAnswersCount = 0;

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<WordEntity> synonyms = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var that = (VocabularyEntryEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
