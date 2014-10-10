package cz.muni.fi.xharting.classic.test.bootstrap.install;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import cz.muni.fi.xharting.classic.bootstrap.ConditionalInstallationService;
import cz.muni.fi.xharting.classic.metadata.AbstractFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor;

public class ConditionalInstallationTest {

    private static ConditionalInstallationService installationService;

    @BeforeClass
    public static void prepare()
    {
        Set<BeanDescriptor> allDescriptors = new HashSet<BeanDescriptor>();
        for (Class<?> clazz : new Class<?>[] { Bean1.class, Bean2.class, Bean3.class, Bean4.class, Bean5.class, Bean6.class, Bean7.class, Bean8.class, Bean9.class })
        {
            allDescriptors.add(new BeanDescriptor(clazz));
        }
        installationService = new ConditionalInstallationService(allDescriptors);
        installationService.filterInstallableComponents();
    }

    @Test
    public void testPrecedence()
    {
        assertTrue(installationService.getInstallableManagedBeanDescriptorMap().containsKey("alpha"));
        assertEquals(installationService.getInstallableManagedBeanDescriptorMap().get("alpha").getJavaClass(), Bean1.class);
    }

    @Test
    public void testClassDependencies()
    {
        assertFalse(installationService.getInstallableManagedBeanDescriptorMap().containsKey("bravo"));
    }

    @Test
    public void testDependencies()
    {
        assertTrue(installationService.getInstallableManagedBeanDescriptorMap().containsKey("charlie"));
        assertEquals(installationService.getInstallableManagedBeanDescriptorMap().get("charlie").getJavaClass(), Bean4.class);
    }

    @Test
    public void testGenericDependencies()
    {
        assertTrue(installationService.getInstallableManagedBeanDescriptorMap().containsKey("delta"));
        assertEquals(installationService.getInstallableManagedBeanDescriptorMap().get("delta").getJavaClass(), Bean7.class);
    }

    @Test
    public void testExplicitVeto()
    {
        assertFalse(installationService.getInstallableManagedBeanDescriptorMap().containsKey("bean8"));
        for (AbstractFactoryDescriptor factory : installationService.getInstallableFactoryDescriptors())
        {
            if (factory.getName().equals("factory8"))
            {
                fail();
            }
        }
    }

    @Test
    public void testExplicitInstallation()
    {
        assertTrue(installationService.getInstallableManagedBeanDescriptorMap().containsKey("bean9"));
        for (AbstractFactoryDescriptor factory : installationService.getInstallableFactoryDescriptors())
        {
            if (factory.getName().equals("factory9"))
            {
                return;
            }
        }
        fail();
    }
}
