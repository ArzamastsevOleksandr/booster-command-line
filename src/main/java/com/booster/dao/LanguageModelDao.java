package com.booster.dao;

import com.booster.mapper.LanguageModelMapper;
import com.booster.model.LanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageModelDao {

    private final JdbcTemplate jdbcTemplate;

    public List<LanguageModel> findAll() {
        return jdbcTemplate.query("select * from Language", new LanguageModelMapper());
    }

}
