/**
 * 
 */
package com.github.phantomthief.dao.resolver.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.ClassUtils;

import com.github.phantomthief.dao.annotation.ReturnId;
import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.resolver.TemplateResolver;
import com.github.phantomthief.dao.template.Template;
import com.github.phantomthief.dao.template.impl.CallbackBasedTemplate;
import com.github.phantomthief.dao.template.impl.ReturnIdUpdateTemplate;
import com.github.phantomthief.dao.template.impl.ReturnKeyTemplate;
import com.github.phantomthief.dao.template.impl.RowBasedTemplate;
import com.github.phantomthief.dao.template.impl.UpdateTemplate;
import com.github.phantomthief.dao.util.MapKeyFunction;
import com.github.phantomthief.dao.util.PluginUtils;
import com.github.phantomthief.dao.util.PluginsHolder;
import com.github.phantomthief.dao.util.ResultCombiner;
import com.github.phantomthief.dao.util.ResultSupplier;

/**
 * @author w.vela
 */
public class DefaultTemplateResolver implements TemplateResolver {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    private final Map<Method, Template<Object, Throwable>> cached = new ConcurrentHashMap<>();

    @SuppressWarnings({ "unchecked" })
    @Override
    public <R, X extends Throwable> Template<R, X> resolve(Method method, SqlContext context) {
        return (Template<R, X>) cached.computeIfAbsent(method, m -> doResolve(m, context));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Template<Object, Throwable> doResolve(Method method, SqlContext context) {
        Class<?> returnType = method.getReturnType();
        PluginsHolder allPlugins = PluginsHolder.merge(PluginUtils.extractPlugin(method),
                context.getPluginsFromParams());
        RowMapper<?> rowMapper = allPlugins.getPlugin(RowMapper.class);
        if (rowMapper == null) {
            rowMapper = getPossibleRowMapper(method);
        }
        if (List.class == returnType) {
            return new RowBasedTemplate(rowMapper);
        }
        if (ClassUtils.isAssignable(KeyHolder.class, returnType)) {
            return (Template) new ReturnKeyTemplate();
        }
        if (context.getKeyHolder() != null) {
            return (Template) new UpdateTemplate(context.getKeyHolder());
        }
        if (method.getAnnotation(ReturnId.class) != null) {
            return (Template) new ReturnIdUpdateTemplate();
        }

        ResultCombiner<?> combiner = allPlugins.getPlugin(ResultCombiner.class);
        if (combiner == null) {
            MapKeyFunction mapKeyFunction = allPlugins.getPlugin(MapKeyFunction.class);
            if (mapKeyFunction != null && rowMapper != null) {
                combiner = new MapResultCombiner(rowMapper, mapKeyFunction);
            }
        }
        if (combiner != null) {
            ResultSupplier<?> supplier = allPlugins.getPlugin(ResultSupplier.class);
            if (supplier == null) {
                supplier = getDefaultSupplier(returnType);
            }
            if (supplier != null) {
                return new CallbackBasedTemplate(supplier, combiner);
            }
        }
        return null;
    }

    /**
     * @param returnType
     * @return
     */
    private ResultSupplier<?> getDefaultSupplier(Class<?> returnType) {
        if (ClassUtils.isAssignable(Map.class, returnType)) {
            if (returnType.isInterface()) {
                return () -> new LinkedHashMap<>();
            } else {
                return () -> {
                    try {
                        return returnType.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        logger.error("fail to init type:{}", returnType, e);
                        throw new RuntimeException(e);
                    }
                };
            }
        }
        return null;
    }

    private Class<?> getGenericType(Type type, int index) {
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[index];
        } else {
            return null;
        }
    }

    private class MapResultCombiner<K, V> implements ResultCombiner<Map<K, V>> {

        private final RowMapper<V> rowMapper;
        private final MapKeyFunction<K, V> mapKeyFunction;

        /**
         * @param rowMapper
         * @param mapKeyFunction
         */
        private MapResultCombiner(RowMapper<V> rowMapper, MapKeyFunction<K, V> mapKeyFunction) {
            this.rowMapper = rowMapper;
            this.mapKeyFunction = mapKeyFunction;
        }

        /* (non-Javadoc)
         * @see com.github.phantomthief.dao.util.ResultCombiner#combine(java.sql.ResultSet, java.lang.Object)
         */
        @Override
        public void combine(ResultSet rs, Map<K, V> result) throws SQLException {
            V mapRow = rowMapper.mapRow(rs, 0);
            if (mapRow != null) {
                result.put(mapKeyFunction.getKey(mapRow), mapRow);
            }
        }
    }

    private RowMapper<?> getPossibleRowMapper(Method method) {
        Class<?> returnType = method.getReturnType();
        Class<?> rowType = null;
        if (ClassUtils.isAssignable(Collection.class, returnType)) {
            rowType = getGenericType(method.getGenericReturnType(), 0);
        } else if (ClassUtils.isAssignable(Map.class, returnType)) {
            rowType = getGenericType(method.getGenericReturnType(), 1);
        }
        if (rowType != null) {
            return new BeanPropertyRowMapper<>(rowType);
        } else {
            return null;
        }
    }
}
