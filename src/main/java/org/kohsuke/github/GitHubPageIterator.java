package org.kohsuke.github;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

public abstract class GitHubPageIterator<Page> implements Iterator<Page> {

    static <P> GitHubPageIterator<P> ofSingleton(final P page) {
        return new GitHubPageIterator<>((Class<P>) page.getClass()) {
            boolean fetched = false;
            @Override
            protected void fetch() {
                if (!fetched) {
                    fetched = true;
                    next = page;
                }
            }
        };
    }

    /**
     * The page that will be returned when {@link #next()} is called.
     *
     * <p>
     * Will be {@code null} after {@link #next()} is called.
     * </p>
     * <p>
     * Will not be {@code null} after {@link #fetch()} is called if a new page was fetched.
     * </p>
     */
    protected Page next;
    protected final Class<Page> pageType;

    public GitHubPageIterator(Class<Page> pageType) {
        this.pageType = pageType;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        fetch();
        return next != null;
    }

    /**
     * Gets the next page.
     *
     * @return the next page.
     */
    @Nonnull
    public Page next() {
        fetch();
        Page result = next;
        if (result == null)
            throw new NoSuchElementException();
        next = null;
        return result;
    }

    protected abstract void fetch();

    GitHubResponse<Page> finalResponse() {
        throw new UnsupportedOperationException("No respons.");
    }
}
