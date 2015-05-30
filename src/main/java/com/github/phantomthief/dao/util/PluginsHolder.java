/**
 * 
 */
package com.github.phantomthief.dao.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author w.vela
 */
public class PluginsHolder {

    private Map<Class<?>, Object> plugins = new HashMap<Class<?>, Object>();

    public boolean contains(Class<?> type) {
        return getPlugin(type) != null;
    }

    public <T> void putPlugin(Class<?> type, T instance) {
        if (type == null) {
            throw new NullPointerException("type is null");
        }
        plugins.put(type, instance);
    }

    public <T> T getPlugin(Class<T> type) {
        return type.cast(plugins.get(type));
    }

    public static PluginsHolder merge(PluginsHolder... plugins) {
        PluginsHolder result = new PluginsHolder();
        if (plugins != null) {
            for (PluginsHolder pluginsHolder : plugins) {
                if (pluginsHolder != null) {
                    result.plugins.putAll(pluginsHolder.plugins);
                }
            }
        }
        return result;
    }
}
