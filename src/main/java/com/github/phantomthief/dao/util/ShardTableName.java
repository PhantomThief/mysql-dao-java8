/**
 * 
 */
package com.github.phantomthief.dao.util;

/**
 * @author w.vela
 */
public interface ShardTableName {

    public String getTableName(String tablePrefix, Number shardKey);
}
