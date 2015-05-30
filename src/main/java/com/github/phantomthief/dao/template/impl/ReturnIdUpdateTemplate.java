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
public class ReturnIdUpdateTemplate implements Template<Number, SQLException> {

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.template.Template#execute(org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate, com.github.phantomthief.dao.context.SqlContext)
     */
    @Override
    public Number execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context)
            throws SQLException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(context.getSQL(), context.getParams(), keyHolder);
        return keyHolder.getKey();
    }

}
