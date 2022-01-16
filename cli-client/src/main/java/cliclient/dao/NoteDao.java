package cliclient.dao;

import cliclient.model.Note;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class NoteDao {

    private static final RowMapper<Note> RS_2_NOTE = (rs, i) -> Note.builder()
            .id(rs.getLong("id"))
            .content(rs.getString("content"))
            .build();

    private final JdbcTemplate jdbcTemplate;

    // todo: DaoOperations component with common dao actions
    public long add(String content) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                            insert into note
                            (content)
                            values (?)""",
                    new String[]{"id"});
            ps.setString(1, content);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    // todo: check in service if it is null
    public Integer countWithId(Long id) {
        return jdbcTemplate.queryForObject("""
                        select count(*)
                        from note
                        where id = %s""".formatted(id),
                Integer.class);
    }

    public void delete(Long id) {
        jdbcTemplate.update("""
                delete from note
                where id = %s""".formatted(id));
    }

    public void removeTagAssociationsById(Long id) {
        jdbcTemplate.update("""
                delete from note__tag__jt
                where note_id = %s""".formatted(id));
    }

    public Note findById(long id) {
        return jdbcTemplate.queryForObject("""
                        select *
                        from note
                        where id = %s""".formatted(id),
                RS_2_NOTE);
    }

    public void addTag(String tag, long noteId) {
        jdbcTemplate.update("""
                        insert into note__tag__jt
                        (note_id, tag)
                        values (?, ?)""",
                noteId, tag);
    }

    public List<Note> findAll() {
        return jdbcTemplate.query("""
                        select *
                        from note""",
                RS_2_NOTE);
    }

    public Map<Long, Set<String>> queryForNoteId2Tags() {
        return jdbcTemplate.query("""
                        select *
                        from note__tag__jt""",
                noteId2TagsResultSetExtractor());
    }

    private ResultSetExtractor<Map<Long, Set<String>>> noteId2TagsResultSetExtractor() {
        return rs -> {
            Map<Long, Set<String>> noteId2Tags = new HashMap<>();
            while (rs.next()) {
                noteId2Tags.computeIfAbsent(rs.getLong("note_id"), k -> new HashSet<>())
                        .add(rs.getString("tag"));
            }
            return noteId2Tags;
        };
    }

    public List<String> findTagsByNoteId(long id) {
        return jdbcTemplate.query("""
                        select tag
                        from note__tag__jt
                        where note_id = %s""".formatted(id),
                (rs, i) -> rs.getString("tag"));
    }

    public void addTagsToNote(List<String> tags, long noteId) {
        jdbcTemplate.batchUpdate("""
                        insert into note__tag__jt
                        (note_id, tag)
                        values (?, ?)""",
                tagsBatchPreparedStatementSetter(tags, noteId)
        );
    }

    private BatchPreparedStatementSetter tagsBatchPreparedStatementSetter(List<String> tags, long id) {
        return new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String tag = tags.get(i);
                ps.setLong(1, id);
                ps.setString(2, tag);
            }

            @Override
            public int getBatchSize() {
                return tags.size();
            }
        };
    }

}
