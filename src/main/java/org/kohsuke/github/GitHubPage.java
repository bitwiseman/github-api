package org.kohsuke.github;

interface GitHubPage<I> {
    /**
     * Wraps up the retrieved object and return them. Only called once.
     *
     * @return the items
     */
    I[] getItems();
}
