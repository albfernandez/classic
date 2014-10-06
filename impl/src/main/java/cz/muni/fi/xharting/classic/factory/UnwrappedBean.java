package cz.muni.fi.xharting.classic.factory;

import static org.jboss.solder.reflection.Reflections.invokeMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import cz.muni.fi.xharting.classic.scope.stateless.StatelessScoped;
import cz.muni.fi.xharting.classic.util.CdiUtils;

/**
 * Represents a Seam unwrapping method. The stateless scope is used to force the container to create a proxy and thus the
 * per-method-invocation behavior is achieved.
 * 
 * @author Jozef Hartinger
 * 
 */
public class UnwrappedBean extends AbstractLegacyFactory<Object> {

    private Method method;
    // no need to store the name of the host, since it is the same as the name of the unwrapped bean itself
    private Class<?> type;
    private Class<?> hostType;

    public UnwrappedBean(String name, Class<?> hostType, Type genericBeanType, Method method, BeanManager manager) {
        super(name, StatelessScoped.class, manager);
        this.method = method;
        this.hostType = hostType;
        this.type = method.getReturnType();
        addTypes(Object.class, genericBeanType);
    }

    @Override
    public Object create(CreationalContext<Object> creationalContext) {

        CdiUtils.ManagedBeanInstance<?> host = CdiUtils.lookupBeanByInternalName(getName(), hostType, getManager());

        try {
            return invokeMethod(true, method, type, host.getInstance());
        } finally {
            host.getCreationalContext().release();
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return hostType;
    }

    @Override
    public String toString() {
        return "UnwrappedBean [method=" + method + ", getName()=" + getName() + "]";
    }

}
