/**
 * 
 */
package com.github.phantomthief.dao.util;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author w.vela
 */
public interface ResultCombiner<R> {

    public void combine(ResultSet rs, R result) throws SQLException;

}
