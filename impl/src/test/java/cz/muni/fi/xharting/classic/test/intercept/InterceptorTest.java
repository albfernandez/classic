package cz.muni.fi.xharting.classic.test.intercept;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InterceptionType;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.muni.fi.xharting.classic.intercept.ClassicInterceptorBinding;

@RunWith(Arquillian.class)
public class InterceptorTest {

    @Inject
    private InterceptedBean bean;

    @Inject
    private BeanManager manager;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", true, false, BooleanInterceptor.class, IntegerInterceptor.class,
            InterceptedBean.class, InterceptorBindings.class, NotInterceptedBean.class).addAsWebInfResource(
            "cz/muni/fi/xharting/classic/test/intercept/beans.xml", "beans.xml");
    }

    @Test
    public void testPassivationCapable() {
        assertTrue(isPassivationCapable(BooleanInterceptor.class));
        assertFalse(isPassivationCapable(IntegerInterceptor.class));
    }

    private boolean isPassivationCapable(Class<?> clazz) {
        List<Interceptor<?>> interceptors = manager.resolveInterceptors(InterceptionType.AROUND_INVOKE,
            new ClassicInterceptorBinding.ClassicInterceptorBindingLiteral(clazz));
        assertEquals(1, interceptors.size());
        return interceptors.get(0) instanceof PassivationCapable;
    }

    @Test
    public void testInterception() {
        assertFalse(bean.getBool1());
        assertTrue(bean.getBool2());
        assertEquals(0, bean.getInt1());
        assertEquals(10, bean.getInt2());
    }

    @Test
    public void testInterceptorBypassing(@Named("notInterceptedBean") NotInterceptedBean bean) {
        // should not throw NoConversationException
        // should not throw IllegalStateException since injection is not performed
        // result should not be modified by IntegerInterceptor
        assertEquals(0, bean.ping());
    }

}
