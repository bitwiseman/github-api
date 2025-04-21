package org.kohsuke.github;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.function.Consumer;

// TODO: Auto-generated Javadoc

/**
 * May be used for any item that has pagination information. Iterates over paginated {@code P} objects (not the items
 * inside the page). Also exposes {@link #finalResponse()} to allow getting a full {@link GitHubResponse}{@code
 *
<P>
 * } after iterating completes.
 *
 * Works for array responses, also works for search results which are single instances with an array of items inside.
 *
 * This class is not thread-safe. Any one instance should only be called from a single thread.
 *
 * @author Liam Newman
 */
class GitHubPaginatedEndpointArrayPageIterator<Item> extends GitHubPaginatedEndpointPageIterator<GithubArrayPage<Item>, Item> {

    final Class<Item[]> receiverType;

    GitHubPaginatedEndpointArrayPageIterator(GitHubClient client, Class<Item[]> receiverType, GitHubRequest request, int pageSize, Consumer<Item> itemInitializer) {
        super(client, (Class<GithubArrayPage<Item>>)new GithubArrayPage<Item>().getClass(), request, pageSize, itemInitializer);
        this.receiverType = receiverType;
    }

    @Override
    @NotNull
    protected GitHubResponse<GithubArrayPage<Item>> sendNextRequest() throws IOException {
        GitHubResponse<Item[]> response = client.sendRequest(nextRequest,
                (connectorResponse) -> GitHubResponse.parseBody(connectorResponse, receiverType));
        return new GitHubResponse<>(response, new GithubArrayPage<>(response.body()));
    }

}
