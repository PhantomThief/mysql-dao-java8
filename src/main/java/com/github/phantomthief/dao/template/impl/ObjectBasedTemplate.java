/**
 * 
 */
package com.github.phantomthief.dao.template.impl;

import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.template.Template;

/**
 * @author w.vela
 */
public class ObjectBasedTemplate<R> implements Template<R, SQLException> {

    private final RowMapper<R> rowMapper;

    /**
     * @param rowMapper
     */
    public ObjectBasedTemplate(RowMapper<R> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.template.Template#execute(org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate, com.github.phantomthief.dao.context.PlainSqlContext)
     */
    @Override
    public R execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context)
            throws SQLException {
        try {
            return jdbcTemplate.queryForObject(context.getSQL(), context.getParams(), rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}
