package com.booster.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TagDao {

    private final JdbcTemplate jdbcTemplate;

    public void add(String name) {
        jdbcTemplate.update("insert into tag (name) values (?)", name);
    }

}
