package org.kohsuke.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * Search commits.
 *
 * @author Marc de Verdelhan
 * @see GitHub#searchCommits() GitHub#searchCommits()
 */
public class GHCommitSearchBuilder extends GHSearchBuilder<GHCommit> {

    /**
     * The enum Sort.
     */
    public enum Sort {

        /** The author date. */
        AUTHOR_DATE,
        /** The committer date. */
        COMMITTER_DATE
    }

    @SuppressFBWarnings(
            value = { "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD" },
            justification = "JSON API")
    private static class CommitSearchResult extends SearchResult<GHCommit> {
        private GHCommit[] items;

        @Override
        GHCommit[] getItems(GitHub root) {
            for (GHCommit commit : items) {
                String repoName = getRepoName(commit.url);
                try {
                    GHRepository repo = root.getRepository(repoName);
                    commit.wrapUp(repo);
                } catch (IOException ioe) {
                }
            }
            return items;
        }
    }

    /**
     * @param commitUrl
     *            a commit URL
     * @return the repo name ("username/reponame")
     */
    private static String getRepoName(String commitUrl) {
        if (StringUtils.isBlank(commitUrl)) {
            return null;
        }
        int indexOfUsername = (GitHubClient.GITHUB_URL + "/repos/").length();
        String[] tokens = commitUrl.substring(indexOfUsername).split("/", 3);
        return tokens[0] + '/' + tokens[1];
    }

    /**
     * Instantiates a new GH commit search builder.
     *
     * @param root
     *            the root
     */
    GHCommitSearchBuilder(GitHub root) {
        super(root, CommitSearchResult.class);
    }

    /**
     * Author gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder author(String v) {
        return q("author:" + v);
    }

    /**
     * Author date gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder authorDate(String v) {
        return q("author-date:" + v);
    }

    /**
     * Author email gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder authorEmail(String v) {
        return q("author-email:" + v);
    }

    /**
     * Author name gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder authorName(String v) {
        return q("author-name:" + v);
    }

    /**
     * Committer gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder committer(String v) {
        return q("committer:" + v);
    }

    /**
     * Committer date gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder committerDate(String v) {
        return q("committer-date:" + v);
    }

    /**
     * Committer email gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder committerEmail(String v) {
        return q("committer-email:" + v);
    }

    /**
     * Committer name gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder committerName(String v) {
        return q("committer-name:" + v);
    }

    /**
     * Hash gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder hash(String v) {
        return q("hash:" + v);
    }

    /**
     * Is gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder is(String v) {
        return q("is:" + v);
    }

    /**
     * Merge gh commit search builder.
     *
     * @param merge
     *            the merge
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder merge(boolean merge) {
        return q("merge:" + Boolean.valueOf(merge).toString().toLowerCase());
    }

    /**
     * Order gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder order(GHDirection v) {
        req.with("order", v);
        return this;
    }

    /**
     * Org gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder org(String v) {
        return q("org:" + v);
    }

    /**
     * Parent gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder parent(String v) {
        return q("parent:" + v);
    }

    /**
     * Search terms.
     *
     * @param term
     *            the term
     * @return the GH commit search builder
     */
    public GHCommitSearchBuilder q(String term) {
        super.q(term);
        return this;
    }

    /**
     * Repo gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder repo(String v) {
        return q("repo:" + v);
    }

    /**
     * Sort gh commit search builder.
     *
     * @param sort
     *            the sort
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder sort(Sort sort) {
        req.with("sort", sort);
        return this;
    }

    /**
     * Tree gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder tree(String v) {
        return q("tree:" + v);
    }

    /**
     * User gh commit search builder.
     *
     * @param v
     *            the v
     * @return the gh commit search builder
     */
    public GHCommitSearchBuilder user(String v) {
        return q("user:" + v);
    }

    /**
     * Gets the api url.
     *
     * @return the api url
     */
    @Override
    protected String getApiUrl() {
        return "/search/commits";
    }
}
