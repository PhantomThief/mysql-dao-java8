/**
 * 
 */
package com.github.phantomthief.dao.template.impl;

import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.template.Template;

/**
 * @author w.vela
 */
public class ReturnKeyTemplate implements Template<KeyHolder, SQLException> {

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.template.Template#execute(org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate, com.github.phantomthief.dao.context.SqlContext)
     */
    @Override
    public KeyHolder execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context)
            throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(context.getSQL(), context.getParams(), keyHolder);
        return keyHolder;
    }

}
