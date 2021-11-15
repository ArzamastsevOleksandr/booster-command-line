package com.booster.dao;

import com.booster.dao.params.AddTagToNoteDaoParams;
import com.booster.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
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

    public long add(String content) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into note (content) values (?)",
                    new String[]{"id"});
            ps.setString(1, content);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public int countWithId(Long id) {
        return jdbcTemplate.queryForObject("select count(*) from note where id = ?", Integer.class, id);
    }

    public void delete(Long id) {
        jdbcTemplate.update("delete from note where id = ?", id);
    }

    public Note findById(long id) {
        return jdbcTemplate.queryForObject("select * from note where id = ?", RS_2_NOTE, id);
    }

    public void addTag(AddTagToNoteDaoParams params) {
        jdbcTemplate.update("insert into note__tag__jt (note_id, tag) values (?, ?)",
                params.getNoteId(), params.getTag());
    }

}
