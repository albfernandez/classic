package cz.muni.fi.xharting.classic.test.factory.unwrap;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class MultipleUnwrapMethodsOnBeanTest {

    @Deployment
    @ShouldThrowException(Exception.class)
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", BrokenBean.class);
    }

    @Test
    public void testMultipleUnwrapMethodsNotAllowedOnBean()
    {
    }
}
