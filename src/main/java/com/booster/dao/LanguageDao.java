package com.booster.dao;

import com.booster.mapper.LanguageMapper;
import com.booster.model.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Language> findAll() {
        return jdbcTemplate.query("select * from Language", new LanguageMapper());
    }

}
