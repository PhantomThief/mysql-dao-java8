/**
 * 
 */
package com.github.phantomthief.dao.util;

/**
 * @author w.vela
 */
public interface MapKeyFunction<K, V> {

    public K getKey(V value);
}
