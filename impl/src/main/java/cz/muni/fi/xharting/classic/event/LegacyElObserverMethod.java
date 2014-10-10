package cz.muni.fi.xharting.classic.event;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;

import org.jboss.solder.el.Expressions;

import cz.muni.fi.xharting.classic.metadata.ElObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.util.CdiUtils;

/**
 * An {@link ObserverMethod} implementation representing an observer method configured in the component descriptor file.
 *
 * @author Jozef Hartinger
 *
 */
public class LegacyElObserverMethod extends AbstractLegacyObserverMethod {

    private Expressions expressions;
    private String methodExpression;

    public LegacyElObserverMethod(ElObserverMethodDescriptor descriptor, TransactionPhase phase, BeanManager manager) {
        super(descriptor.getType(), phase, true, manager);
        this.methodExpression = descriptor.getMethodExpression();
    }

    @Override
    public Class<?> getBeanClass() {
        return Object.class; // defined in EL - there is no Java class representing this class
    }

    @Override
    public void notify(EventPayload event) {
        if (expressions == null) {
            expressions = CdiUtils.lookupBean(Expressions.class, getManager()).getInstance();
        }
        expressions.evaluateMethodExpression(methodExpression);
    }

    @Override
    public String toString() {
        return "LegacyElObserverMethod [methodExpression=" + methodExpression + ", getQualifier()=" + getQualifier() + ", getTransactionPhase()=" + getTransactionPhase() + "]";
    }
}
