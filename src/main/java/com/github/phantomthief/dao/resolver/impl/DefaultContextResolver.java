/**
 * 
 */
package com.github.phantomthief.dao.resolver.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import com.github.phantomthief.dao.annotation.DAO;
import com.github.phantomthief.dao.annotation.SQL;
import com.github.phantomthief.dao.annotation.ShardKey;
import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.context.impl.PlainSqlContext;
import com.github.phantomthief.dao.context.impl.ShardSqlContext;
import com.github.phantomthief.dao.resolver.ContextResolver;
import com.github.phantomthief.dao.util.DataSourceProvider;
import com.github.phantomthief.dao.util.MapKeyFunction;
import com.github.phantomthief.dao.util.PluginsHolder;
import com.github.phantomthief.dao.util.ResultCombiner;
import com.github.phantomthief.dao.util.ResultSupplier;
import com.github.phantomthief.dao.util.ShardDataSourceProvider;
import com.github.phantomthief.dao.util.ShardTableName;
import com.google.common.collect.MapMaker;

/**
 * @author w.vela
 */
public class DefaultContextResolver implements ContextResolver {

    private final DataSourceProvider dataSourceProvider;
    private final ShardDataSourceProvider shardDataSourceProvider;
    private final ConcurrentMap<DataSource, NamedParameterJdbcTemplate> cached = new MapMaker()
            .concurrencyLevel(16) //
            .weakKeys() //
            .makeMap();

    private final Set<Class<?>> pluginTypes = new HashSet<>();

    {
        pluginTypes.add(MapKeyFunction.class);
        pluginTypes.add(ResultCombiner.class);
        pluginTypes.add(ResultSupplier.class);
        pluginTypes.add(ShardTableName.class);
    }

    /**
     * @param dataSourceProvider
     * @param shardDataSourceProvider
     */
    public DefaultContextResolver(DataSourceProvider dataSourceProvider,
            ShardDataSourceProvider shardDataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
        this.shardDataSourceProvider = shardDataSourceProvider;
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.resolver.ContextResolver#resolve(java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public SqlContext resolve(Method method, Object[] args) {
        DAO dao = getAnnotaion(method, DAO.class);
        SQL sql = method.getAnnotation(SQL.class);
        String rawSql = sql.value();
        MapSqlParameterSource params = new MapSqlParameterSource();
        Parameter[] parameters = method.getParameters();
        Number shardKey = null;
        KeyHolder keyHolder = null;
        PluginsHolder pluginsHolder = new PluginsHolder();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object arg = args[i];
            if (arg instanceof KeyHolder) {
                keyHolder = (KeyHolder) arg;
            } else {
                if (arg != null) {
                    boolean isPlugin = false;
                    for (Class<?> iface : arg.getClass().getInterfaces()) {
                        if (pluginTypes.contains(iface)) {
                            pluginsHolder.putPlugin(iface, arg);
                            isPlugin = true;
                        }
                    }
                    if (!isPlugin) {
                        params.addValue(parameter.getName(), arg);
                        if (parameter.getAnnotation(ShardKey.class) != null) {
                            shardKey = (Number) arg;
                        }
                    }
                }
            }
        }
        if (shardKey == null) {
            return new PlainSqlContext(rawSql, params, keyHolder, pluginsHolder);
        } else {
            return new ShardSqlContext(rawSql, shardKey,
                    shardDataSourceProvider.getShardTableName(dao.value()), params, keyHolder,
                    pluginsHolder);
        }
    }

    /* (non-Javadoc)
     * @see com.github.phantomthief.dao.resolver.ContextResolver#jdbcTemplate(java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public NamedParameterJdbcTemplate jdbcTemplate(Method method, Object[] args) {
        DAO dao = getAnnotaion(method, DAO.class);
        DataSource dataSource;
        Number shardKey = null;
        for (int i = 0; i < method.getParameterAnnotations().length; i++) {
            Annotation[] name = method.getParameterAnnotations()[i];
            if (name != null) {
                if (Stream.of(name).anyMatch(a -> a.annotationType() == ShardKey.class)) {
                    shardKey = (Number) args[i];
                    break;
                }
            }
        }
        if (shardKey != null && shardDataSourceProvider != null) {
            dataSource = shardDataSourceProvider.getDataSource(dao.value(), shardKey);
        } else {
            dataSource = dataSourceProvider.getDataSource(dao.value());
        }
        return cached.computeIfAbsent(dataSource, NamedParameterJdbcTemplate::new);
    }

    private static <A extends Annotation> A getAnnotaion(Method method, Class<A> annotationClass) {
        A anno = method.getAnnotation(annotationClass);
        if (anno != null) {
            return anno;
        }
        return method.getDeclaringClass().getAnnotation(annotationClass);
    }
}
