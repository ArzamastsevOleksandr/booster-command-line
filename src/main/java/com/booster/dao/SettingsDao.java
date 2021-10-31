package com.booster.dao;

import com.booster.dao.params.AddSettingsDaoParams;
import com.booster.model.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
        return jdbcTemplate.queryForObject(
                "select * " +
                        "from settings " +
                        "limit 1", RS_TO_SETTINGS);
    }

    private static Long getLongValueOrNull(Object obj) {
        return ofNullable(obj)
                .filter(Long.class::isInstance)
                .map(Long.class::cast)
                .orElse(null);
    }

    public void add(AddSettingsDaoParams params) {
        jdbcTemplate.update(
                "insert into settings " +
                        "(language_id) " +
                        "values (?)",
                params.getLanguageId().orElse(null));
    }

    public void deleteAll() {
        jdbcTemplate.update(
                "delete from settings " +
                        "where id in (select s.id from settings s)");
    }

    public int count() {
        return jdbcTemplate.queryForObject(
                "select count(*) " +
                        "from settings", Integer.class);
    }

}
