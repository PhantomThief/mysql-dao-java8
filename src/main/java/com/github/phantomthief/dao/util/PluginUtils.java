/**
 * 
 */
package com.github.phantomthief.dao.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.phantomthief.dao.annotation.Plugin;

/**
 * @author w.vela
 */
public final class PluginUtils {

    private static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PluginUtils.class);

    private static Map<Method, PluginsHolder> cached = new ConcurrentHashMap<>();

    public static final PluginsHolder extractPlugin(Method method) {
        return cached.computeIfAbsent(method, m -> {
            PluginsHolder result = new PluginsHolder();
            putMap(m.getAnnotationsByType(Plugin.class), result);
            putMap(m.getDeclaringClass().getAnnotationsByType(Plugin.class), result);
            return result;
        });
    }

    private static void putMap(Plugin[] plugins, PluginsHolder map) {
        if (plugins == null) {
            return;
        }
        for (Plugin plugin : plugins) {
            for (Class<?> t : plugin.value()) {
                if (!map.contains(t)) {
                    try {
                        Object obj = t.newInstance();
                        for (Class<?> iface : t.getInterfaces()) {
                            map.putPlugin(iface, obj);
                        }
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.error("Ops.", e);
                    }
                }
            }
        }
    }
}
