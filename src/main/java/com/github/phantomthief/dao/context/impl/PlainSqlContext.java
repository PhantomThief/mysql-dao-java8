/**
 * 
 */
package com.github.phantomthief.dao.context.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.util.PluginsHolder;

/**
 * @author w.vela
 */
public class PlainSqlContext implements SqlContext {

    private final String rawSQL;
    private final MapSqlParameterSource params;
    private final KeyHolder keyHolder;
    private final PluginsHolder pluginHolder;

    public PlainSqlContext(String rawSQL, MapSqlParameterSource params, KeyHolder keyHolder,
            PluginsHolder pluginHolder) {
        this.rawSQL = rawSQL;
        this.params = params;
        this.keyHolder = keyHolder;
        this.pluginHolder = pluginHolder;
    }

    public String getSQL() {
        return rawSQL;
    }

    public MapSqlParameterSource getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "PlainSqlContext [rawSQL=" + rawSQL + ", params=" + params + "]";
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.context.SqlContext#getKeyHolder()
     */
    @Override
    public KeyHolder getKeyHolder() {
        return keyHolder;
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.context.SqlContext#getPluginsFromParams()
     */
    @Override
    public PluginsHolder getPluginsFromParams() {
        return pluginHolder;
    }

}
