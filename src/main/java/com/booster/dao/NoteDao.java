package com.booster.dao;

import com.booster.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NoteDao {

    public static final RowMapper<Note> RS_2_NOTE = (rs, i) -> Note.builder()
            .id(rs.getLong("id"))
            .content(rs.getString("content"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<Note> findAll() {
        return jdbcTemplate.query("select * from note", RS_2_NOTE);
    }

}
