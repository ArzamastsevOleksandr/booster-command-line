package vocabularyservice.language;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "languages")
@Getter
@Setter
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LanguageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;

}
