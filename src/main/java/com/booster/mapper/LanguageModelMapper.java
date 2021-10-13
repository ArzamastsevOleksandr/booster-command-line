package com.booster.mapper;

import com.booster.model.LanguageModel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LanguageModelMapper implements RowMapper<LanguageModel> {

    @Override
    public LanguageModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return LanguageModel.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }

}
