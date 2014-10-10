package cz.muni.fi.xharting.classic.util.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import javax.interceptor.InvocationContext;

/**
 * Utility class for supporting the legacy InvocationContext interface.
 *
 * @author Jozef Hartinger
 *
 */
public abstract class ForwardingInvocationContext implements InvocationContext {

    protected abstract InvocationContext getDelegate();

    @Override
    public Object getTarget() {
        return getDelegate().getTarget();
    }

    @Override
    public Constructor<?> getConstructor() {
        return getDelegate().getConstructor();
    }

    @Override
    public Method getMethod() {
        return getDelegate().getMethod();
    }

    @Override
    public Object[] getParameters() {
        return getDelegate().getParameters();
    }

    @Override
    public void setParameters(Object[] params) {
        getDelegate().setParameters(params);
    }

    @Override
    public Map<String, Object> getContextData() {
        return getDelegate().getContextData();
    }

    @Override
    public Object getTimer() {
        return getDelegate().getTimer();
    }

    @Override
    public Object proceed() throws Exception {
        return getDelegate().proceed();
    }
}
