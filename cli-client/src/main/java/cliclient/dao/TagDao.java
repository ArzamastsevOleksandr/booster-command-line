package cliclient.dao;

import cliclient.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TagDao {

    private static final RowMapper<Tag> RS_2_TAG = (rs, i) -> new Tag(rs.getString("name"));

    private final JdbcTemplate jdbcTemplate;

    public List<Tag> findAll() {
        return jdbcTemplate.query("""
                        select *
                        from tag""",
                RS_2_TAG);
    }

    public String add(String name) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("""
                            insert into tag
                            (name)
                            values (?)""",
                    new String[]{"name"});
            ps.setString(1, name);
            return ps;
        }, keyHolder);
        return keyHolder.getKeyAs(String.class);
    }

    public Tag findByName(String name) {
        return jdbcTemplate.queryForObject("""
                        select *
                        from tag
                        where name = ?""",
                RS_2_TAG,
                name);
    }

    public Integer countWithName(String tag) {
        return jdbcTemplate.queryForObject("""
                        select count(*)
                        from tag
                        where name = ?""",
                Integer.class,
                tag);
    }

    public void addAll(List<String> tagsToCreate) {
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement("""
                    insert into tag (name)
                    select * from (select lower(new_tag) from unnest(?) as new_tag) tags_to_create
                    except
                    select lower(name) from tag""");
            ps.setArray(1, con.createArrayOf("varchar", tagsToCreate.toArray()));
            return ps;
        });
    }

}
