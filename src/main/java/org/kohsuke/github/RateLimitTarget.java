package org.kohsuke.github;

// TODO: Auto-generated Javadoc
/**
 * Specifies the rate limit record of an operation.
 *
 * @see GitHubBuilder#withRateLimitChecker(RateLimitChecker, RateLimitTarget)
 */
public enum RateLimitTarget {
    /**
     * Selects or updates the {@link GHRateLimit#getCore()} record.
     */
    CORE,

    /**
     * Selects or updates the {@link GHRateLimit#getGraphQL()} record.
     */
    GRAPHQL,

    /**
     * Selects or updates the {@link GHRateLimit#getIntegrationManifest()} record.
     */
    INTEGRATION_MANIFEST,

    /**
     * Selects no rate limit.
     *
     * This request uses no rate limit. If the response header includes rate limit information, it will apply to
     * {@link #CORE}.
     */
    NONE,

    /**
     * Selects or updates the {@link GHRateLimit#getSearch()} record.
     */
    SEARCH
}
