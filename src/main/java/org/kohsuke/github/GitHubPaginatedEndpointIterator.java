package org.kohsuke.github;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This class is not thread-safe. Any one instance should only be called from a single thread.
 */
class GitHubPaginatedEndpointIterator<Page extends GitHubPage<Item>, Item> implements Iterator<Item> {

    /**
     * Current batch of items. Each time {@link #next()} is called the next item in this array will be returned. After
     * the last item of the array is returned, when {@link #next()} is called again, a new page of items will be fetched
     * and iterating will continue from the first item in the new page.
     *
     * @see #fetch() {@link #fetch()} for details on how this field is used.
     */
    private Page currentPage;

    /**
     * The index of the next item on the page, the item that will be returned when {@link #next()} is called.
     *
     * @see #fetch() {@link #fetch()} for details on how this field is used.
     */
    private int nextItemIndex;

    private final GitHubPageIterator<Page> pageIterator;

    GitHubPaginatedEndpointIterator(GitHubPageIterator<Page> pageIterator) {
        this.pageIterator = pageIterator;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        fetch();
        return (currentPage != null && currentPage.getItems().length > nextItemIndex);
    }

    /**
     * {@inheritDoc}
     */
    public Item next() {
        if (!hasNext())
            throw new NoSuchElementException();
        return currentPage.getItems()[nextItemIndex++];
    }

    /**
     * Gets the next page worth of data.
     *
     * @return the list
     */
    public List<Item> nextPage() {
        return Arrays.asList(nextPageArray());
    }

    /**
     * Fetch is called at the start of {@link #next()} or {@link #hasNext()} to fetch another page of data if it is
     * needed and available.
     * <p>
     * If there is no current page yet (at the start of iterating), a page is fetched. If {@link #nextItemIndex} points
     * to an item in the current page array, the state is valid - no more work is needed. If {@link #nextItemIndex} is
     * greater than the last index in the current page array, the method checks if there is another page of data
     * available.
     * </p>
     * <p>
     * If there is another page, get that page of data and reset the check {@link #nextItemIndex} to the start of the
     * new page.
     * </p>
     * <p>
     * If no more pages are available, leave the page and index unchanged. In this case, {@link #hasNext()} will return
     * {@code false} and {@link #next()} will throw an exception.
     * </p>
     */
    private void fetch() {
        if ((currentPage == null || currentPage.getItems().length <= nextItemIndex)
                && pageIterator.hasNext()) {
            // On first call, always get next page (may be empty array)
            Page result = Objects.requireNonNull(pageIterator.next());
            currentPage = result;
            nextItemIndex = 0;
        }
    }

    /**
     * Gets the next page worth of data.
     *
     * @return the list
     */
    protected Page currentPage() {
        fetch();
        return currentPage;
    }

    /**
     * Gets the {@link GitHubResponse} for the last page received.
     *
     * @return the {@link GitHubResponse} for the last page received.
     */
    GitHubResponse<Page> lastResponse() {
        return pageIterator.finalResponse();
    }

    /**
     * Gets the next page worth of data.
     *
     * @return the list
     */
    @Nonnull
    Item[] nextPageArray() {
        // if we have not fetched any pages yet, always fetch.
        // If we have fetched at least one page, check hasNext()
        if (currentPage == null) {
            fetch();
        } else if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // Current should never be null after fetch
        Objects.requireNonNull(currentPage);
        Item[] r = currentPage.getItems();
        if (nextItemIndex != 0) {
            r = Arrays.copyOfRange(r, nextItemIndex, r.length);
        }
        nextItemIndex = currentPage.getItems().length;
        return r;
    }
}
