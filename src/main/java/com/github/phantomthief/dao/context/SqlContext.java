/**
 * 
 */
package com.github.phantomthief.dao.context;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import com.github.phantomthief.dao.util.PluginsHolder;

/**
 * @author w.vela
 */
public interface SqlContext {

    public String getSQL();

    public MapSqlParameterSource getParams();

    public KeyHolder getKeyHolder();

    public PluginsHolder getPluginsFromParams();
}
