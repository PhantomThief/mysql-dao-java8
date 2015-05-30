/**
 * 
 */
package com.github.phantomthief.dao.context.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.util.PluginsHolder;
import com.github.phantomthief.dao.util.ShardTableName;

/**
 * @author w.vela
 */
public class ShardSqlContext implements SqlContext {

    private static final Pattern TABLE_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    private final String rawSQL;
    private final String replacedSQL;
    private final MapSqlParameterSource params;
    private final KeyHolder keyHolder;
    private final PluginsHolder pluginsHolder;

    public ShardSqlContext(String rawSQL, Number shardKey, ShardTableName shardTableName,
            MapSqlParameterSource params, KeyHolder keyHolder, PluginsHolder pluginsHolder) {
        this.rawSQL = rawSQL;
        this.replacedSQL = transfer(shardKey, shardTableName);
        this.params = params;
        this.keyHolder = keyHolder;
        this.pluginsHolder = pluginsHolder;
    }

    /**
     * @param shardKey
     * @param shardTableName
     * @return
     */
    private String transfer(Number shardKey, ShardTableName shardTableName) {
        Matcher matcher = TABLE_PATTERN.matcher(rawSQL);
        StringBuffer sb = new StringBuffer();
        if (matcher.find()) {
            String tableName = matcher.group(1);
            matcher.appendReplacement(sb, shardTableName.getTableName(tableName, shardKey));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String getSQL() {
        return replacedSQL;
    }

    public MapSqlParameterSource getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "ShardSqlContext [rawSQL=" + rawSQL + ", replacedSQL=" + replacedSQL + ", params="
                + params + "]";
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
        return pluginsHolder;
    }
}
