package com.booster.dao;

import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WordDao {

    public static final RowMapper<Word> rs2Word = (rs, i) -> Word.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<Word> findAll() {
        return jdbcTemplate.query("select * from word w", rs2Word);
    }

    public Word createWordWithName(String name) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "insert into word " +
                            "(name) " +
                            "values (?)", new String[]{"id"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return Word.builder()
                .id(keyHolder.getKey().longValue())
                .name(name)
                .build();
    }

    public Word findByName(String name) {
        return jdbcTemplate.queryForObject("select * from word w where w.name = ?", rs2Word, name);
    }

}
