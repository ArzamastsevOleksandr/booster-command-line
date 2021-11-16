package com.booster.dao;

import com.booster.dao.params.AddTagToNoteDaoParams;
import com.booster.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public class NoteDao {

    public static final RowMapper<Note> RS_2_NOTE = (rs, i) -> Note.builder()
            .id(rs.getLong("id"))
            .content(rs.getString("content"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    public List<Note> findAll() {
        List<Note> notes = jdbcTemplate.query("select * from note", RS_2_NOTE);
        Map<Long, Set<String>> noteId2Tags = jdbcTemplate.query("select * from note__tag__jt ", resultSetExtractor());
        return notes.stream()
                .map(n -> n.toBuilder().tags(noteId2Tags.getOrDefault(n.getId(), Set.of())).build())
                .collect(toList());
    }

    private ResultSetExtractor<Map<Long, Set<String>>> resultSetExtractor() {
        return rs -> {
            Map<Long, Set<String>> veId2Values = new HashMap<>();
            while (rs.next()) {
                veId2Values.computeIfAbsent(rs.getLong("note_id"), k -> new HashSet<>())
                        .add(rs.getString("tag"));
            }
            return veId2Values;
        };
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

    // todo: make dao simple, group all logic in the service?
    public Note findById(long id) {
        Note note = jdbcTemplate.queryForObject("select * from note where id = ?", RS_2_NOTE, id);
        List<String> tags = jdbcTemplate.query("select tag from note__tag__jt where note_id = ?",
                (rs, i) -> rs.getString("tag"), id);
        return note.toBuilder().tags(new HashSet<>(tags)).build();
    }

    public void addTag(AddTagToNoteDaoParams params) {
        jdbcTemplate.update("insert into note__tag__jt (note_id, tag) values (?, ?)",
                params.getNoteId(), params.getTag());
    }

}
