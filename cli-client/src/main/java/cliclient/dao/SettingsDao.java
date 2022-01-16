package cliclient.dao;

import cliclient.model.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class SettingsDao {

    private static final RowMapper<Settings> RS_TO_SETTINGS = (rs, i) -> Settings.builder()
            .languageId(getLongValueOrNull(rs.getObject("language_id")))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public Settings findOne() {
        return jdbcTemplate.queryForObject("""
                        select *
                        from settings""",
                RS_TO_SETTINGS);
    }

    private static Long getLongValueOrNull(Object obj) {
        return ofNullable(obj)
                .filter(Long.class::isInstance)
                .map(Long.class::cast)
                .orElse(null);
    }

    public long add(Long languageId) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> con.prepareStatement("""
                        insert into settings
                        (language_id)
                        values (%s)""".formatted(languageId),
                new String[]{"id"}), keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from settings");
    }

    public Integer count() {
        return jdbcTemplate.queryForObject("""
                        select count(*)
                        from settings""",
                Integer.class);
    }

    public Settings findById(long id) {
        return jdbcTemplate.queryForObject("""
                        select *
                        from settings
                        where id = %s""".formatted(id),
                RS_TO_SETTINGS);
    }

}
