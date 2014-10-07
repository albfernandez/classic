package cz.muni.fi.xharting.classic.metadata;

import static org.jboss.solder.reflection.Reflections.setFieldValue;

import java.lang.reflect.Field;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;

/**
 * Represents a field annotated with the {@link In} annotation.
 *
 * @author Jozef Hartinger
 *
 */

public class InjectionPointDescriptor extends AbstractManagedFieldDescriptor {

    private final boolean create;

    public InjectionPointDescriptor(In in, Field field, BeanDescriptor bean) {
        super(in.value(), in.required(), in.scope(), field, bean);
        this.create = in.create();
    }

    public InjectionPointDescriptor(InjectionPointDescriptor original, BeanDescriptor bean) {
        this(original.getSpecifiedName(), original.isRequired(), original.getSpecifiedScope(), original.getField(), bean, original.isCreate());
    }

    public InjectionPointDescriptor(String name, boolean required, ScopeType specifiedScope, Field field, BeanDescriptor bean, boolean create) {
        super(name, required, specifiedScope, field, bean);
        this.create = create;
    }

    public boolean isCreate() {
        return create;
    }

    public void set(Object target, Object value) {
        setFieldValue(false, getField(), target, value);
    }

    @Override
    public String toString() {
        return "InjectionPointDescriptor [getField()=" + getField() + "]";
    }
}
