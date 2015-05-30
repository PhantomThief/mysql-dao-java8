/**
 * 
 */
package com.github.phantomthief.dao.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.resolver.ContextResolver;
import com.github.phantomthief.dao.resolver.TemplateResolver;
import com.github.phantomthief.dao.template.Template;

import javassist.util.proxy.MethodHandler;

/**
 * @author w.vela
 */
public class JdbcTemplateHandler implements MethodHandler, InvocationHandler {

    private final TemplateResolver templateResolver;
    private final ContextResolver contextResolver;

    /**
     * @param templateResolver
     * @param contextResolver
     */
    public JdbcTemplateHandler(TemplateResolver templateResolver, ContextResolver contextResolver) {
        this.templateResolver = templateResolver;
        this.contextResolver = contextResolver;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlContext context = contextResolver.resolve(method, args);
        NamedParameterJdbcTemplate jdbcTemplate = contextResolver.jdbcTemplate(method, args);
        Template<Object, Throwable> template = templateResolver.resolve(method, context);
        return template.execute(jdbcTemplate, context);
    }

    /* (non-Javadoc)
     * @see javassist.util.proxy.MethodHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.Object[])
     */
    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
            throws Throwable {
        SqlContext context = contextResolver.resolve(thisMethod, args);
        NamedParameterJdbcTemplate jdbcTemplate = contextResolver.jdbcTemplate(thisMethod, args);
        Template<Object, Throwable> template = templateResolver.resolve(thisMethod, context);
        return template.execute(jdbcTemplate, context);
    }

}
