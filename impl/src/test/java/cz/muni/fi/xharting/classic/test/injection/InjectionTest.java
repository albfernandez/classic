package cz.muni.fi.xharting.classic.test.injection;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.RequiredException;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InjectionTest {

    @Inject
    private Alpha alpha;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war").addPackage(InjectionTest.class.getPackage());
    }

    @Test
    public void testInjectionPointWithImplicitName() {
        assertNotNull(alpha.getBravo());
        assertEquals("pong", alpha.getBravo().ping());
    }

    @Test
    public void testInjectionPointWithExplicitName() {
        assertNotNull(alpha.getCharlie());
        assertEquals("pong", alpha.getCharlie().ping());
    }

    @Test
    public void testInjectionPointWithDisabledBeanCreation(@Named("foxtrot") Foxtrot foxtrot) {
        assertNull(alpha.getDelta());

        assertEquals("pong", foxtrot.ping()); // invoke method to create the foxtrot component
        assertNotNull(alpha.getFoxtrot());
        assertEquals("pong", alpha.getFoxtrot().ping());
    }

    @Test
    public void testInjectionPointWithAutoCreate() {
        assertNotNull(alpha.getEcho());
        assertEquals("pong", alpha.getEcho().ping());
    }

    @Test
    public void testCyclicInjection(@Named("alpha") Alpha alpha) {
        assertNotNull(alpha.getCyclicBean1());
        assertEquals("cyclicBean1", alpha.getCyclicBean1().echo());
        assertNotNull(alpha.getCyclicBean1().getCyclicBean2());
        assertEquals("cyclicBean2", alpha.getCyclicBean1().getCyclicBean2().echo());
        assertNotNull(alpha.getCyclicBean1().getCyclicBean2().getCyclicBean1());
        assertEquals("cyclicBean1", alpha.getCyclicBean1().getCyclicBean2().getCyclicBean1().echo());
    }

    @Test
    public void testRequiredException(@Named("golf") Golf golf) {
        try {
            golf.ping();
            Assert.fail();
        } catch (RequiredException e) {
            // expected
        }
    }

    @Test
    public void testClassHierarchy() {
        alpha.getHotelSubclass().checkInjection(); // will throw AssertionError if injection does not work
    }

    @Test
    public void testInjectionOfEjbAndIntoEjb()
    {
        assertNotNull(alpha.getIndia());
        assertTrue(alpha.getIndia().injected());
    }
}
