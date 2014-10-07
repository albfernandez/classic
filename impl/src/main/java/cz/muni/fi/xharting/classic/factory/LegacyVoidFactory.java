package cz.muni.fi.xharting.classic.factory;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanAttributes;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.reflection.HierarchyDiscovery;
import org.jboss.weld.bean.BeanIdentifiers;
import org.jboss.weld.bean.StringBeanIdentifier;
import org.jboss.weld.serialization.spi.BeanIdentifier;

import cz.muni.fi.xharting.classic.bijection.RewritableContextManager;
import cz.muni.fi.xharting.classic.metadata.FactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.OutjectionPointDescriptor;
import cz.muni.fi.xharting.classic.scope.stateless.StatelessScoped;
import cz.muni.fi.xharting.classic.util.CdiUtils;
import cz.muni.fi.xharting.classic.util.CdiUtils.ManagedBeanInstance;

/**
 * Represents a Seam 2 factory method with void return type. Special handling is necessary.
 * 
 * @author Jozef Hartinger
 * 
 */
public class LegacyVoidFactory extends LegacyFactory {

    private final OutjectionPointDescriptor field;
    private Class<? extends Annotation> scope;
    private RewritableContextManager context;

    public LegacyVoidFactory(FactoryDescriptor descriptor, BeanManager manager) {
        super(descriptor, manager);
        field = findMatchingField(descriptor);
        scope = field.getCdiScope();
        initType(descriptor);
        identifier = createId(descriptor, this);
    }

    private static BeanIdentifier createId(FactoryDescriptor descriptor, LegacyVoidFactory attributes) {
        return new StringBeanIdentifier(BeanIdentifiers.forSyntheticBean(attributes, attributes.field.getType()));
    }

    private OutjectionPointDescriptor findMatchingField(FactoryDescriptor factory) {
        for (OutjectionPointDescriptor field : factory.getBean().getOutjectionPoints()) {
            if (factory.getName().equals((field.getName()))) {
                return field;
            }
        }
        throw new IllegalArgumentException("Void factory method " + factory.getMethod() + " must have a matching outjected field named " + factory.getName());
    }

    @Override
    protected void initType(FactoryDescriptor descriptor) {
        if (field != null) {
            addTypes(new HierarchyDiscovery(field.getType()).getTypeClosure());
        }
    }

    // We use stateless since the outjected value is put into the RewritableContext which has higher precedence
    @Override
    public Class<? extends Annotation> getScope() {
        return StatelessScoped.class;
    }

    @Override
    protected Object create(ManagedBeanInstance<?> host) {
        super.create(host);
        return getRewritableContextManager().get(getName(), scope);
    }

    protected RewritableContextManager getRewritableContextManager() {
        if (context == null) {
            context = CdiUtils.lookupBean(RewritableContextManager.class, getManager()).getInstance();
        }
        return context;
    }

    @Override
    public String toString() {
        return "LegacyVoidFactory [getMethod()=" + getMethod() + ", getName()=" + getName() + "]";
    }
}
