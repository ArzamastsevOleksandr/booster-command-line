package com.booster.mapper;

import com.booster.model.Language;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LanguageMapper implements RowMapper<Language> {

    @Override
    public Language mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Language.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

}
