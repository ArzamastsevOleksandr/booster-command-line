package com.booster.dao;

import com.booster.model.Language;
import com.booster.model.LanguageBeingLearned;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageBeingLearnedDao {

    private final JdbcTemplate jdbcTemplate;

    public List<LanguageBeingLearned> findAll() {
        return jdbcTemplate.query(
                "select lbl.id as id, l.name, lbl.created_at, l.id as l_id " +
                        "from language_being_learned lbl " +
                        "inner join language l " +
                        "on l.id = lbl.language_id",
                (rs, i) -> LanguageBeingLearned.builder()
                        .id(rs.getLong("id"))
                        .createdAt(rs.getTimestamp("created_at"))
                        .language(Language.builder()
                                .id(rs.getLong("l_id"))
                                .name(rs.getString("name"))
                                .build())
                        .build());
    }

    public void add(long languageId) {
        jdbcTemplate.update("insert into language_being_learned " +
                "(language_id) " +
                "values " +
                "(?)", languageId);
    }

}
