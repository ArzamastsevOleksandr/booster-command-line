package com.booster.dao;

import com.booster.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TagDao {

    private static final RowMapper<Tag> RS_2_TAG = (rs, i) -> new Tag(rs.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findAll() {
        return jdbcTemplate.query("select * from tag", RS_2_TAG);
    }

    public void add(String name) {
        jdbcTemplate.update("insert into tag (name) values (?)", name);
    }

}
