package org.kohsuke.github;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.annotation.CheckForNull;

// TODO: Auto-generated Javadoc
/**
 * A branch in a repository.
 *
 * @author Yusuke Kokubo
 */
@SuppressFBWarnings(
        value = { "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD",
                "URF_UNREAD_FIELD" },
        justification = "JSON API")
public class GHBranch extends GitHubInteractiveObject {
    private GHRepository owner;

    private String name;
    private Commit commit;
    @JsonProperty("protected")
    private boolean protection;
    private String protectionUrl;

    /**
     * Instantiates a new GH branch.
     *
     * @param name
     *            the name
     */
    @JsonCreator
    GHBranch(@JsonProperty(value = "name", required = true) String name) {
        Objects.requireNonNull(name);
        this.name = name;
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

        /** The sha. */
        String sha;

        /** The url. */
        @SuppressFBWarnings(value = "UUF_UNUSED_FIELD", justification = "We don't provide it in API now")
        String url;
    }

    /**
     * Gets owner.
     *
     * @return the repository that this branch is in.
     */
    @SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "Expected behavior")
    public GHRepository getOwner() {
        return owner;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Is protected boolean.
     *
     * @return true if the push to this branch is restricted via branch protection.
     */
    public boolean isProtected() {
        return protection;
    }

    /**
     * Gets protection url.
     *
     * @return API URL that deals with the protection of this branch.
     */
    public URL getProtectionUrl() {
        return GitHubClient.parseURL(protectionUrl);
    }

    /**
     * Gets protection.
     *
     * @return the protection
     * @throws IOException
     *             the io exception
     */
    public GHBranchProtection getProtection() throws IOException {
        return root().createRequest().setRawUrlPath(protectionUrl).fetch(GHBranchProtection.class);
    }

    /**
     * Gets sha 1.
     *
     * @return The SHA1 of the commit that this branch currently points to.
     */
    public String getSHA1() {
        return commit.sha;
    }

    /**
     * Disables branch protection and allows anyone with push access to push changes.
     *
     * @throws IOException
     *             if disabling protection fails
     */
    public void disableProtection() throws IOException {
        root().createRequest().method("DELETE").setRawUrlPath(protectionUrl).send();
    }

    /**
     * Enables branch protection to control what commit statuses are required to push.
     *
     * @return GHBranchProtectionBuilder for enabling protection
     * @see GHCommitStatus#getContext() GHCommitStatus#getContext()
     */
    public GHBranchProtectionBuilder enableProtection() {
        return new GHBranchProtectionBuilder(this);
    }

    /**
     * Merge a branch into this branch.
     *
     * @param headBranch
     *            the branch whose head will be merged
     *
     * @param commitMessage
     *            the commit message
     *
     * @return the merge {@link GHCommit} created, or {@code null} if the base already contains the head (nothing to
     *         merge).
     *
     * @throws IOException
     *             if merging fails
     */
    @CheckForNull
    public GHCommit merge(GHBranch headBranch, String commitMessage) throws IOException {
        return merge(headBranch.getName(), commitMessage);
    }

    /**
     * Merge a ref into this branch.
     *
     * @param head
     *            the ref name that will be merged into this branch. Follows the usual ref naming rules, could be a
     *            branch name, tag, or commit sha.
     *
     * @param commitMessage
     *            the commit message
     *
     * @return the merge {@link GHCommit} created, or {@code null} if the base already contains the head (nothing to
     *         merge).
     *
     * @throws IOException
     *             if merging fails
     */
    @CheckForNull
    public GHCommit merge(String head, String commitMessage) throws IOException {
        GHCommit result = root().createRequest()
                .withUrlPath(owner.getApiTailUrl("merges"))
                .method("POST")
                .with("commit_message", commitMessage)
                .with("base", this.name)
                .with("head", head)
                .fetch(GHCommit.class);

        if (result != null) {
            result.wrapUp(owner);
        }

        return result;
    }

    /**
     * Gets the api route.
     *
     * @return the api route
     */
    String getApiRoute() {
        return owner.getApiTailUrl("/branches/" + name);
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        final String url = owner != null ? owner.getUrl().toString() : "unknown";
        return "Branch:" + name + " in " + url;
    }

    /**
     * Wrap.
     *
     * @param repo
     *            the repo
     * @return the GH branch
     */
    GHBranch wrap(GHRepository repo) {
        this.owner = repo;
        return this;
    }
}
