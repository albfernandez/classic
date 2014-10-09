package cz.muni.fi.xharting.classic.test.bootstrap;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BeanRegistrationTest {

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Alpha.class, Bravo.class, Charlie.class, Delta.class, DeltaFactory.class,
            Echo.class, EchoLocal.class, Foxtrot.class);
    }

    @Test
    public void testNamesAndScoped(BeanManager manager) {
        assertEquals(RequestScoped.class, manager.resolve(manager.getBeans("alpha")).getScope());
        assertEquals(ApplicationScoped.class, manager.resolve(manager.getBeans("bravo")).getScope());
        assertEquals(SessionScoped.class, manager.resolve(manager.getBeans("b1")).getScope());
        assertEquals(RequestScoped.class, manager.resolve(manager.getBeans("b2")).getScope());
        assertEquals(RequestScoped.class, manager.resolve(manager.getBeans("charlie")).getScope());
        assertEquals(Dependent.class, manager.resolve(manager.getBeans("echo")).getScope());
    }

    @Test
    public void testInitializers(@Named("alpha") Alpha alpha, @Named("bravo") Bravo bravo, @Named("b1") Bravo b1, @Named("b2") Bravo b2, Foxtrot foxtrot) {
        foxtrot.ping();
        alpha.ping();
        bravo.ping();
        b1.ping();
        b2.ping();
        assertTrue(foxtrot.isInitCalled());
        assertTrue(alpha.isInitCalled());
        assertTrue(bravo.isInitCalled());
        assertTrue(b1.isInitCalled());
        assertTrue(b2.isInitCalled());
    }

    @Test
    public void testFactoryBeans(BeanManager manager) {
        assertEquals(ApplicationScoped.class, manager.resolve(manager.getBeans("d1")).getScope());
        assertEquals(SessionScoped.class, manager.resolve(manager.getBeans("d2")).getScope());
        assertEquals(RequestScoped.class, manager.resolve(manager.getBeans("d3")).getScope());
    }

    @Test
    public void testFactories(@Named("d1") Delta d1, @Named("d2") Delta d2, @Named("d3") Delta d3) {
        assertEquals("d1", d1.getName());
        assertEquals("d2", d2.getName());
        assertEquals("d3", d3.getName());
    }

    @Test
    @InSequence(1000)
    public void testDesctructors() {
        assertTrue(Foxtrot.isDestroyCalled());
        assertTrue(Alpha.isDestroyCalled());
        assertTrue(Bravo.isDestroyCalled());
    }
}
