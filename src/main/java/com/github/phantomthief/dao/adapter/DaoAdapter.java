/**
 * 
 */
package com.github.phantomthief.dao.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.github.phantomthief.dao.proxy.JdbcTemplateHandler;
import com.github.phantomthief.dao.resolver.impl.DefaultContextResolver;
import com.github.phantomthief.dao.resolver.impl.DefaultTemplateResolver;
import com.github.phantomthief.dao.util.DataSourceProvider;
import com.github.phantomthief.dao.util.ShardDataSourceProvider;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * @author w.vela
 */
public class DaoAdapter {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    private DataSourceProvider dataSourceProvider;
    private ShardDataSourceProvider shardDataSourceProvider;

    public static final DaoAdapter newAdapter() {
        return new DaoAdapter();
    }

    public DaoAdapter setDataSourceProvider(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
        return this;
    }

    public DaoAdapter setShardDataSourceProvider(ShardDataSourceProvider dataSourceProvider) {
        this.shardDataSourceProvider = dataSourceProvider;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> iface) {
        /*return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class<?>[] { iface },
                new JdbcTemplateHandler(new DefaultTemplateResolver(),
                        new DefaultContextResolver(dataSourceProvider, shardDataSourceProvider)));*/

        ProxyFactory factory = new ProxyFactory();
        if (iface.isInterface()) {
            factory.setInterfaces(new Class<?>[] { iface });
        } else {
            factory.setSuperclass(iface);
        }
        try {
            T x = (T) factory.create(new Class[] {}, new Object[] {});
            factory.setFilter(this::filterMethod);
            ((Proxy) x).setHandler(new JdbcTemplateHandler(new DefaultTemplateResolver(),
                    new DefaultContextResolver(dataSourceProvider, shardDataSourceProvider)));
            return x;
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            logger.error("Ops.", e);
            e.printStackTrace();
            return null;
        }
    }

    private boolean filterMethod(Method method) {
        if (method.isDefault()) {
            return false;
        }
        int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers)) {
            return true;
        }
        if (Modifier.isInterface(modifiers)) {
            return true;
        } else {
            return false;
        }
    }

}
