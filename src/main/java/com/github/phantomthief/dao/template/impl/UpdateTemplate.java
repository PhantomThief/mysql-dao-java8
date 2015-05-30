/**
 * 
 */
package com.github.phantomthief.dao.template.impl;

import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.template.Template;

/**
 * @author w.vela
 */
public class UpdateTemplate implements Template<Integer, SQLException> {

    private final KeyHolder keyHolder;

    public UpdateTemplate() {
        this(null);
    }

    /**
     * @param keyHolder
     */
    public UpdateTemplate(org.springframework.jdbc.support.KeyHolder keyHolder) {
        this.keyHolder = keyHolder;
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.template.Template#execute(org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate, com.github.phantomthief.dao.context.SqlContext)
     */
    @Override
    public Integer execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context)
            throws SQLException {
        if (keyHolder == null) {
            return jdbcTemplate.update(context.getSQL(), context.getParams());
        } else {
            return jdbcTemplate.update(context.getSQL(), context.getParams(), keyHolder);
        }
    }

}
