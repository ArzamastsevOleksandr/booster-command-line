package com.booster.dao;

import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WordDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Word> findAll() {
        return jdbcTemplate.query("select * from word", (rs, i) -> Word.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build());
    }

    // todo: separate service that checks if the word/ve/v/lbl exists and creates if false
    public Word findByNameOrCreateAndGet(String name) {
        Optional<Word> optionalWord = findByName(name);
        if (optionalWord.isPresent()) {
            return optionalWord.get();
        } else {
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
    }

    private Optional<Word> findByName(String name) {
        try {
            Word word = jdbcTemplate.queryForObject("select * from word " +
                            "where name = ?",
                    (rs, i) -> Word.builder()
                            .id(rs.getLong("id"))
                            .name(rs.getString("name"))
                            .build(),
                    name);
            return Optional.of(word);
        } catch (Exception e) {
            // todo: handle all exceptions properly
            return Optional.empty();
        }
    }

}
