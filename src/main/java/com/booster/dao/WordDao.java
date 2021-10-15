package com.booster.dao;

import com.booster.model.Word;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

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

}
