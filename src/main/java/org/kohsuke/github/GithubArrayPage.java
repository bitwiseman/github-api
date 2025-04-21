package org.kohsuke.github;

// TODO: Auto-generated Javadoc

import java.util.function.Consumer;

/**
 * Represents the result of a search.
 *
 * @author Kohsuke Kawaguchi
 * @param <T>
 *            the generic type
 */
class GithubArrayPage<T> implements GitHubPage<T> {

    private final T[] items;

    public GithubArrayPage() {
        this.items = null;
    }

    public GithubArrayPage(T[] items) {
        this.items = items;
    }

    public T[] getItems() {
        return items;
    }
}
