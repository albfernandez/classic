package org.jboss.seam.classic.test.scope;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jboss.seam.classic.scope.StatelessScoped;

@StatelessScoped
public class Book {

    private static final AtomicInteger identifier = new AtomicInteger();
    private static volatile int lastDestroyed = 0;
    
    private volatile int id;

    public Book() {
    }

    public int getId() {
        return id;
    }
    
    @PostConstruct
    public void postConstruct()
    {
        this.id = identifier.getAndIncrement();
        System.out.println("Created: " + id);
    }
    
    @PreDestroy
    public void preDestroy()
    {
        lastDestroyed = id;
        System.out.println("Destroyed: " + id);
    }

    public static int getLastDestroyed() {
        return lastDestroyed;
    }
}