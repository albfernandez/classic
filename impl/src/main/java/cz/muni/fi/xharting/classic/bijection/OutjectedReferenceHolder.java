package cz.muni.fi.xharting.classic.bijection;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * An implementation of a rewritable context used for emulation of push/pull context model used in Seam. This component
 * represents an inner rewritable object store. Every time a component attempts to modify the content of a context, this change
 * is reflected within the rewritable context. Likewise, when a component instance is requested, the rewritable context is
 * checked first. If the requested component instance is found in the rewritable context, it is served from there. Otherwise,
 * the request is delegated to the CDI context. The rewritable context internally uses a threadsafe map structure.
 *
 * @author Jozef Hartinger
 *
 */
@Vetoed
public class OutjectedReferenceHolder implements Serializable {

    private static final long serialVersionUID = -3467806759929526350L;

    private Map<String, Object> values = new ConcurrentHashMap<String, Object>();

    public Object get(String name) {
        return values.get(name);
    }

    public void put(String name, Object value) {
        if (value == null) {
            values.remove(name);
        } else {
            values.put(name, value);
        }
    }

    public boolean contains(String name) {
        return values.containsKey(name);
    }

    /**
     * A qualifier used to distinguish between different instances of {@link OutjectedReferenceHolder} placed in different
     * context.
     *
     * @author Jozef Hartinger
     *
     */
    @Qualifier
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    public @interface ScopeQualifier {

        Class<? extends Annotation> value();

        @SuppressWarnings("all")
        public class ScopeQualifierLiteral extends AnnotationLiteral<ScopeQualifier> implements ScopeQualifier {

            private static final long serialVersionUID = 813098538363934584L;
            private Class<? extends Annotation> value;

            private ScopeQualifierLiteral(Class<? extends Annotation> value) {
                this.value = value;
            }

            @Override
            public Class<? extends Annotation> value() {
                return value;
            }

            private static Map<Class<? extends Annotation>, ScopeQualifierLiteral> values = new HashMap<Class<? extends Annotation>, ScopeQualifier.ScopeQualifierLiteral>();

            public static ScopeQualifierLiteral valueOf(Class<? extends Annotation> context) {
                if (!values.containsKey(context)) {
                    values.put(context, new ScopeQualifierLiteral(context));
                }
                return values.get(context);
            }
        }
    }

    @Override
    public String toString() {
        return "Rewritable context [values=" + values + "]";
    }
}
