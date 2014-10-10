package cz.muni.fi.xharting.classic.util;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import cz.muni.fi.xharting.classic.Seam2ManagedBean;

public class CdiUtils {

    private CdiUtils() {
    }

    public static <T> ManagedBeanInstance<T> lookupBean(Class<T> clazz, BeanManager manager) {
        Set<Bean<?>> beans = manager.getBeans(clazz);
        return lookupBean(beans, clazz, manager);
    }

    public static <T> ManagedBeanInstance<T> lookupBeanByName(String beanName, Class<T> clazz, BeanManager manager) {
        Set<Bean<?>> beans = manager.getBeans(beanName);
        return lookupBean(beans, clazz, manager);
    }

    public static <T> ManagedBeanInstance<T> lookupBeanByInternalName(String beanName, Class<T> clazz, BeanManager manager) {
        Set<Bean<?>> beans = manager.getBeans(clazz, new Seam2ManagedBean.Seam2ManagedBeanLiteral(beanName));
        return lookupBean(beans, clazz, manager);
    }

    @SuppressWarnings("unchecked")
    public static <T> ManagedBeanInstance<T> lookupBean(Set<Bean<?>> beans, Class<T> clazz, BeanManager manager) {
        Bean<T> bean = (Bean<T>) manager.resolve(beans);
        if (bean == null) {
            throw new UnsatisfiedResolutionException("Unable to lookup " + clazz.getName());
        }
        CreationalContext<T> ctx = (CreationalContext<T>) manager.createCreationalContext(bean);
        T instance = (T) manager.getReference(bean, clazz, ctx);
        return new ManagedBeanInstance<T>(instance, ctx, bean);
    }

    public static class ManagedBeanInstance<T> {
        private final T instance;
        private final CreationalContext<T> creationalContext;
        private final Bean<T> bean;

        public ManagedBeanInstance(T instance, CreationalContext<T> creationalContext, Bean<T> bean) {
            this.instance = instance;
            this.creationalContext = creationalContext;
            this.bean = bean;
        }

        public T getInstance() {
            return instance;
        }

        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }

        public Bean<T> getBean() {
            return bean;
        }

        public void release() {
            if (creationalContext != null && Dependent.class.equals(bean.getScope())) {
                creationalContext.release();
            }
        }
    }

    /**
     * Determines whether a given scope is active or not.
     */
    public static boolean isContextActive(Class<? extends Annotation> scope, BeanManager manager) {
        try {
            return manager.getContext(scope).isActive();
        } catch (ContextNotActiveException e) {
            return false;
        }
    }
}
