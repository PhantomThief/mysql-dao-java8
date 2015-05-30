/**
 * 
 */
package com.github.phantomthief.dao.template.impl;

import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.template.Template;
import com.github.phantomthief.dao.util.ResultCombiner;
import com.github.phantomthief.dao.util.ResultSupplier;

/**
 * @author w.vela
 */
public class CallbackBasedTemplate<R> implements Template<R, SQLException> {

    private final ResultSupplier<R> resultFactory;
    private final ResultCombiner<R> resultCombiner;

    /**
     * @param resultFactory
     * @param resultCombiner
     */
    public CallbackBasedTemplate(ResultSupplier<R> resultFactory,
            ResultCombiner<R> resultCombiner) {
        this.resultFactory = resultFactory;
        this.resultCombiner = resultCombiner;
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.template.Template#execute(org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate, com.github.phantomthief.dao.context.PlainSqlContext)
     */
    @Override
    public R execute(NamedParameterJdbcTemplate jdbcTemplate, SqlContext context)
            throws SQLException {
        R result = resultFactory.get();
        jdbcTemplate.query(context.getSQL(), context.getParams(),
                rs -> resultCombiner.combine(rs, result));
        return result;
    }

}
