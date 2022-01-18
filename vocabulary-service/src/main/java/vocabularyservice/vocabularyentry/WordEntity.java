package vocabularyservice.vocabularyentry;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table(name = "words")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

}
