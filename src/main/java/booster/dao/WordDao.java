package booster.dao;

import booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WordDao {

    private static final RowMapper<Word> rs2Word = (rs, i) -> Word.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<Word> findAll() {
        return jdbcTemplate.query("""
                        select *
                        from word""",
                rs2Word);
    }

    //    todo: unique name in db
    public long createWithName(String name) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> con.prepareStatement("""
                        insert into word
                        (name)
                        values ('%s')""".formatted(name),
                new String[]{"id"}), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public Word findByName(String name) {
        return jdbcTemplate.queryForObject("""
                        select *
                        from word
                        where name = '%s'""".formatted(name),
                rs2Word);
    }

    public Integer countWithName(String name) {
        return jdbcTemplate.queryForObject("""
                        select count(*)
                        from word
                        where name = '%s'""".formatted(name),
                Integer.class);
    }

}
