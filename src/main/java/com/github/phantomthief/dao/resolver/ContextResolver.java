/**
 * 
 */
package com.github.phantomthief.dao.resolver;

import java.lang.reflect.Method;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.phantomthief.dao.context.SqlContext;

/**
 * @author w.vela
 */
public interface ContextResolver {

    public SqlContext resolve(Method method, Object[] args);

    public NamedParameterJdbcTemplate jdbcTemplate(Method method, Object[] args);
}
