/**
 * 
 */
package com.github.phantomthief.dao.util;

import javax.sql.DataSource;

/**
 * @author w.vela
 */
public interface ShardDataSourceProvider {

    public DataSource getDataSource(String dataSourceName, Number shardKey);

    public ShardTableName getShardTableName(String dataSourceName);
}
