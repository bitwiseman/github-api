/*
 * The MIT License
 *
 * Copyright (c) 2013, Luca Milanesio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kohsuke.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.net.URL;

// TODO: Auto-generated Javadoc
/**
 * Commit detail inside a {@link GHPullRequest}.
 *
 * @author Luca Milanesio
 * @see GHPullRequest#listCommits() GHPullRequest#listCommits()
 */
@SuppressFBWarnings(
        value = { "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD",
                "URF_UNREAD_FIELD" },
        justification = "JSON API")
public class GHPullRequestCommitDetail {

    /**
     * Create default GHPullRequestCommitDetail instance
     */
    public GHPullRequestCommitDetail() {
    }

    private GHPullRequest owner;

    /**
     * Wrap up.
     *
     * @param owner
     *            the owner
     */
    void wrapUp(GHPullRequest owner) {
        this.owner = owner;
    }

    /**
     * The type Tree.
     */
    public static class Tree {

        /**
         * Create default Tree instance
         */
        public Tree() {
        }

        /** The sha. */
        String sha;

        /** The url. */
        String url;

        /**
         * Gets sha.
         *
         * @return the sha
         */
        public String getSha() {
            return sha;
        }

        /**
         * Gets url.
         *
         * @return the url
         */
        public URL getUrl() {
            return GitHubClient.parseURL(url);
        }
    }

    /**
     * The type Commit.
     */
    public static class Commit {

        /**
         * Create default Commit instance
         */
        public Commit() {
        }

        /** The author. */
        GitUser author;

        /** The committer. */
        GitUser committer;

        /** The message. */
        String message;

        /** The tree. */
        Tree tree;

        /** The url. */
        String url;

        /** The comment count. */
        Integer commentCount;

        /**
         * Gets author.
         *
         * @return the author
         */
        public GitUser getAuthor() {
            return author;
        }

        /**
         * Gets committer.
         *
         * @return the committer
         */
        public GitUser getCommitter() {
            return committer;
        }

        /**
         * Gets message.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets url.
         *
         * @return the url
         */
        public URL getUrl() {
            return GitHubClient.parseURL(url);
        }

        /**
         * Gets comment count.
         *
         * @return the comment count
         * @deprecated Use {@link #getCommentCount()}
         */
        @Deprecated
        public int getComment_count() {
            return getCommentCount();
        }

        /**
         * Gets comment count.
         *
         * @return the comment count
         */
        public Integer getCommentCount() {
            return commentCount;
        }

        /**
         * Gets tree.
         *
         * @return the tree
         */
        public Tree getTree() {
            return tree;
        }
    }

    /**
     * The type CommitPointer.
     */
    public static class CommitPointer {

        /**
         * Create default CommitPointer instance
         */
        public CommitPointer() {
        }

        /** The sha. */
        String sha;

        /** The url. */
        String url;

        /** The html url. */
        String htmlUrl;

        /**
         * Gets url.
         *
         * @return the url
         */
        public URL getUrl() {
            return GitHubClient.parseURL(url);
        }

        /**
         * Gets html url.
         *
         * @return the html url
         */
        public URL getHtmlUrl() {
            return GitHubClient.parseURL(htmlUrl);
        }

        /**
         * Gets html url.
         *
         * @return the html url
         * @deprecated Use {@link #getHtmlUrl()}
         */
        @Deprecated
        public URL getHtml_url() {
            return getHtmlUrl();
        }

        /**
         * Gets sha.
         *
         * @return the sha
         */
        public String getSha() {
            return sha;
        }
    }

    /** The sha. */
    String sha;

    /** The commit. */
    Commit commit;

    /** The url. */
    String url;

    /** The html url. */
    String htmlUrl;

    /** The comments url. */
    String commentsUrl;

    /** The parents. */
    CommitPointer[] parents;

    /**
     * Gets sha.
     *
     * @return the sha
     */
    public String getSha() {
        return sha;
    }

    /**
     * Gets commit.
     *
     * @return the commit
     */
    public Commit getCommit() {
        return commit;
    }

    /**
     * Gets api url.
     *
     * @return the api url
     */
    public URL getApiUrl() {
        return GitHubClient.parseURL(url);
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public URL getUrl() {
        return GitHubClient.parseURL(htmlUrl);
    }

    /**
     * Gets comments url.
     *
     * @return the comments url
     */
    public URL getCommentsUrl() {
        return GitHubClient.parseURL(commentsUrl);
    }

    /**
     * Get parents commit pointer [ ].
     *
     * @return the commit pointer [ ]
     */
    public CommitPointer[] getParents() {
        CommitPointer[] newValue = new CommitPointer[parents.length];
        System.arraycopy(parents, 0, newValue, 0, parents.length);
        return newValue;
    }
}
