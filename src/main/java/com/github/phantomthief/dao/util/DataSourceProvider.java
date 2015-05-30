/**
 * 
 */
package com.github.phantomthief.dao.util;

import javax.sql.DataSource;

/**
 * @author w.vela
 */
public interface DataSourceProvider {

    public DataSource getDataSource(String dataSourceName);
}
