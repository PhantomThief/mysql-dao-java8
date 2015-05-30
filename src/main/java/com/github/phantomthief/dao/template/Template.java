/**
 * 
 */
package com.github.phantomthief.dao.template;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.phantomthief.dao.context.SqlContext;

/**
 * @author w.vela
 */
public interface Template<R, X extends Throwable> {

    public R execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context) throws X;
}
