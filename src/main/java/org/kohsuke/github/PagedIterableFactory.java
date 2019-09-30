package org.kohsuke.github;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * {@link Iterable} that returns {@link PagedIterator}
 *
 * @author Kohsuke Kawaguchi
 */
/* package */ class PagedIterableFactory {

    static <T> PagedIterable<T> create(final Class<T[]> clazz, final GitHub root, final String tailApiUrl, Consumer<T> wrapItemConsumer) {
        return create(clazz, root.retrieve(), tailApiUrl, wrapItemConsumer);
    }

    static <T> PagedIterable<T> create(String preview, final Class<T[]> clazz, final GitHub root, final String tailApiUrl, Consumer<T> wrapItemConsumer) {
        return new PagedIterable<T>() {
            public PagedIterator<T> _iterator(int pageSize) {
                return new PagedIterator<T>(root.retrieve().withPreview(preview).asIterator(tailApiUrl, clazz, pageSize)) {
                    @Override
                    protected void wrapUp(T[] page) {
                        for (T item : page) {
                            wrapItemConsumer.accept(item);
                        }
                    }
                };
            }
        };
    }

    static <T> PagedIterable<T> create(String preview, final Class<T[]> clazz, final Requester req, final String tailApiUrl, Consumer<T> wrapItemConsumer) {
        return new PagedIterable<T>() {
            public PagedIterator<T> _iterator(int pageSize) {
                return new PagedIterator<T>(req.withPreview(preview).asIterator(tailApiUrl, clazz, pageSize)) {
                    @Override
                    protected void wrapUp(T[] page) {
                        for (T item : page) {
                            wrapItemConsumer.accept(item);
                        }
                    }
                };
            }
        };
    }

    static <T> PagedIterable<T> create(final Class<T[]> clazz, final Requester req, final String tailApiUrl, Consumer<T> wrapItemConsumer) {
        return new PagedIterable<T>() {
            public PagedIterator<T> _iterator(int pageSize) {
                return new PagedIterator<T>(req.asIterator(tailApiUrl, clazz, pageSize)) {
                    @Override
                    protected void wrapUp(T[] page) {
                        for (T item : page) {
                            wrapItemConsumer.accept(item);
                        }
                    }
                };
            }
        };
    }


}
