package org.kohsuke.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

// TODO: Auto-generated Javadoc
/**
 * {@link PagedIterable} enhanced to report search result specific information.
 *
 * @author Kohsuke Kawaguchi
 * @param <Item>
 *            the type parameter
 */
@SuppressFBWarnings(
        value = { "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_FIELD",
                "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR" },
        justification = "Constructed by JSON API")
public class PagedSearchIterable<Item> extends PagedIterable<Item> {

    private final GitHubPaginatedEndpoint<? extends SearchResult<Item>, Item> paginatedEndpoint;

    /**
     * Instantiates a new git hub page contents iterable.
     */
    <Result extends SearchResult<Item>> PagedSearchIterable(GitHubPaginatedEndpoint<Result, Item> paginatedEndpoint) {
        super(paginatedEndpoint);
        this.paginatedEndpoint = paginatedEndpoint;
    }

    /**
     * Returns the total number of hit, including the results that's not yet fetched.
     *
     * @return the total count
     */
    public int getTotalCount() {
        // populate();
        return paginatedEndpoint.itemIterator().currentPage().totalCount;
    }

    /**
     * Is incomplete boolean.
     *
     * @return the boolean
     */
    public boolean isIncomplete() {
        // populate();
        return paginatedEndpoint.itemIterator().currentPage().incompleteResults;
    }
}
