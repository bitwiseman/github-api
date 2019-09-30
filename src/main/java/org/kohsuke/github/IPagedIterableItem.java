package org.kohsuke.github;

public abstract class IPagedIterableItem<T> {
    abstract void wrapUp(T parent);
}
