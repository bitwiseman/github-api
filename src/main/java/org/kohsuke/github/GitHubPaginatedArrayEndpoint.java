package org.kohsuke.github;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import javax.annotation.Nonnull;

class GitHubPaginatedArrayEndpoint<Item> extends GitHubPaginatedEndpoint<GithubArrayPage<Item>, Item> {

    static <I> GitHubPaginatedArrayEndpoint<I> fromArray(I[] array) {
        final GithubArrayPage<I> page = new GithubArrayPage<>(array);
        return new GitHubPaginatedArrayEndpoint<>(null, null, (Class<I[]>) array.getClass(), null) {
            @Nonnull
            @Override
            public GitHubPageIterator<GithubArrayPage<I>> pageIterator() {
                return GitHubPageIterator.ofSingleton(page);
            }
        };
    }

    private final Class<Item[]> receiverType;

    GitHubPaginatedArrayEndpoint(GitHubClient client,
                                 GitHubRequest request,
                                 Class<Item[]> receiverType,
                                 Consumer<Item> itemInitializer) {
        super(client, request, (Class<GithubArrayPage<Item>>) new GithubArrayPage<Item>().getClass(),
                (Class<Item>) receiverType.getComponentType(), itemInitializer);
        this.receiverType = receiverType;
    }

    @NotNull
    @Override
    public GitHubPageIterator<GithubArrayPage<Item>> pageIterator() {
        return new GitHubPaginatedEndpointArrayPageIterator<>(client, receiverType, request, pageSize, itemInitializer);
    }
}