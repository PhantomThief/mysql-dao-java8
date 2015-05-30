/**
 * 
 */
package com.github.phantomthief.dao.template.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.template.Template;

/**
 * @author w.vela
 */
public class RowBasedTemplate<E> implements Template<List<E>, SQLException> {

    private final RowMapper<E> rowMapper;

    /**
     * @param rowMapper
     */
    public RowBasedTemplate(RowMapper<E> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.template.Template#execute(org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate, com.github.phantomthief.dao.context.PlainSqlContext)
     */
    @Override
    public List<E> execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context)
            throws SQLException {
        return jdbcTemplate.query(context.getSQL(), context.getParams(), rowMapper);
    }
}
