package org.kohsuke.github;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

/**
 * {@link GitHubPaginatedEndpoint} implementation that take a {@link Consumer} that initializes all the items on each
 * page as they are retrieved.
 *
 * {@link GitHubPaginatedEndpoint} is immutable and thread-safe, but the iterator returned from {@link #iterator()} is
 * not. Any one instance of iterator should only be called from a single thread.
 *
 * @author Liam Newman
 * @param <Item>
 *            the type of items on each page
 */
class GitHubPaginatedEndpoint<Page extends GitHubPage<Item>, Item> implements Iterable<Item> {

    static <P extends GitHubPage<I>, I> GitHubPaginatedEndpoint<P, I> fromPage(Class<I> itemType, P page) {
        return new GitHubPaginatedEndpoint<>(null, null, (Class<P>) page.getClass(), itemType, null) {
            @Nonnull
            @Override
            public GitHubPageIterator<P> pageIterator() {
                return GitHubPageIterator.ofSingleton(page);
            }
        };
    }


    protected final GitHubClient client;

    protected final Consumer<Item> itemInitializer;

    protected final Class<Item> itemType;

    /**
     * Page size. 0 is default.
     */
    protected int pageSize = 0;
    protected final Class<Page> pageType;

    protected final GitHubRequest request;

    /**
     * Instantiates a new git hub page contents iterable.
     *
     * @param client
     *            the client
     * @param request
     *            the request
     * @param pageType
     *            the receiver type
     * @param itemInitializer
     *            the item initializer
     */
    GitHubPaginatedEndpoint(GitHubClient client,
            GitHubRequest request,
            Class<Page> pageType,
            Class<Item> itemType,
            Consumer<Item> itemInitializer) {
        this.client = client;
        this.request = request;
        this.pageType = pageType;
        this.itemType = itemType;
        this.itemInitializer = itemInitializer;
    }

    @Nonnull
    public final GitHubPaginatedEndpointIterator<Page, Item> itemIterator() {
        return new GitHubPaginatedEndpointIterator<>(this.pageIterator());
    }

    @Nonnull
    @Override
    public final Iterator<Item> iterator() {
        return this.itemIterator();
    }

    /**
     *
     * @return
     */
    @Nonnull
    public GitHubPageIterator<Page> pageIterator() {
        return new GitHubPaginatedEndpointPageIterator<>(client, pageType, request, pageSize, itemInitializer);
    }

    /**
     * Eagerly walk {@link Iterable} and return the result in an array.
     *
     * @return the list
     * @throws IOException
     *             if an I/O exception occurs.
     */
    @Nonnull
    public final Item[] toArray() throws IOException {
        return toArray(itemIterator());
    }
    /**
     * Eagerly walk {@link Iterable} and return the result in a list.
     *
     * @return the list
     * @throws IOException
     *             if an I/O Exception occurs
     */
    @Nonnull
    public final List<Item> toList() throws IOException {
        return Collections.unmodifiableList(Arrays.asList(this.toArray()));
    }

    /**
     * Eagerly walk {@link Iterable} and return the result in a set.
     *
     * @return the set
     * @throws IOException
     *             if an I/O Exception occurs
     */
    @Nonnull
    public final Set<Item> toSet() throws IOException {
        return Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(this.toArray())));
    }

    /**
     * Sets the pagination size.
     *
     * <p>
     * When set to non-zero, each API call will retrieve this many entries.
     *
     * @param size
     *            the size
     * @return the paged iterable
     */
    public final GitHubPaginatedEndpoint<Page, Item> withPageSize(int size) {
        this.pageSize = size;
        return this;
    }

    /**
     * Concatenates a list of arrays into a single array.
     *
     * @param type
     *            the type of array to be returned.
     * @param pages
     *            the list of arrays to be concatenated.
     * @param totalLength
     *            the total length of the returned array.
     * @return an array containing all elements from all pages.
     */
    @Nonnull
    private Item[] concatenatePages(Class<Item[]> type, List<Item[]> pages, int totalLength) {

        Item[] result = type.cast(Array.newInstance(type.getComponentType(), totalLength));

        int position = 0;
        for (Item[] page : pages) {
            final int pageLength = Array.getLength(page);
            System.arraycopy(page, 0, result, position, pageLength);
            position += pageLength;
        }
        return result;
    }

    /**
     * Eagerly walk {@link PagedIterator} and return the result in an array.
     *
     * @param iterator
     *            the {@link PagedIterator} to read
     * @return an array of all elements from the {@link PagedIterator}
     * @throws IOException
     *             if an I/O exception occurs.
     */
//    protected final Item[] toArray(final GitHubPaginatedEndpointIterator<Page, Item> iterator) throws IOException {
    protected final Item[] toArray(final GitHubPaginatedEndpointIterator<Page, Item> iterator) throws IOException {
        try {
            ArrayList<Item[]> pages = new ArrayList<>();
            int totalSize = 0;
            Item[] item;
            do {
                item = iterator.nextPageArray();
                totalSize += Array.getLength(item);
                pages.add(item);
            } while (iterator.hasNext());

            Class<Item[]> type = (Class<Item[]>) item.getClass();

            return concatenatePages(type, pages, totalSize);
        } catch (GHException e) {
            // if there was an exception inside the iterator it is wrapped as a GHException
            // if the wrapped exception is an IOException, throw that
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            } else {
                throw e;
            }
        }
    }

    /**
     * Eagerly walk {@link Iterable} and return the result in a {@link GitHubResponse} containing an array of {@code T}
     * items.
     *
     * @return the last response with an array containing all the results from all pages.
     * @throws IOException
     *             if an I/O exception occurs.
     */
    @Nonnull
    final GitHubResponse<Item[]> toResponse() throws IOException {
        GitHubPaginatedEndpointIterator<Page, Item> iterator = itemIterator();
        Item[] items = toArray(iterator);
        GitHubResponse<Page> lastResponse = iterator.lastResponse();
        return new GitHubResponse<>(lastResponse, items);
    }
}
