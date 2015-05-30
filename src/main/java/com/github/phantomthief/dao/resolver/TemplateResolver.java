/**
 * 
 */
package com.github.phantomthief.dao.resolver;

import java.lang.reflect.Method;

import com.github.phantomthief.dao.context.SqlContext;
import com.github.phantomthief.dao.template.Template;

/**
 * @author w.vela
 */
public interface TemplateResolver {

    public <R, X extends Throwable> Template<R, X> resolve(Method method, SqlContext context);
}
