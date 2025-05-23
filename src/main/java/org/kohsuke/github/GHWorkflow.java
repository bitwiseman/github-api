package org.kohsuke.github;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

// TODO: Auto-generated Javadoc
/**
 * A workflow.
 *
 * @author Guillaume Smet
 * @see GHRepository#getWorkflow(long)
 */
public class GHWorkflow extends GHObject {

    private String badgeUrl;

    private String htmlUrl;

    private String name;
    // Not provided by the API.
    @JsonIgnore
    private GHRepository owner;
    private String path;

    private String state;
    /**
     * Create default GHWorkflow instance
     */
    public GHWorkflow() {
    }

    /**
     * Disable the workflow.
     *
     * @throws IOException
     *             the io exception
     */
    public void disable() throws IOException {
        root().createRequest().method("PUT").withUrlPath(getApiRoute(), "disable").send();
    }

    /**
     * Create a workflow dispatch event which triggers a manual workflow run.
     *
     * @param ref
     *            the git reference for the workflow. The reference can be a branch or tag name.
     * @throws IOException
     *             the io exception
     */
    public void dispatch(String ref) throws IOException {
        dispatch(ref, Collections.emptyMap());
    }

    /**
     * Create a workflow dispatch event which triggers a manual workflow run.
     *
     * @param ref
     *            the git reference for the workflow. The reference can be a branch or tag name.
     * @param inputs
     *            input keys and values configured in the workflow file. The maximum number of properties is 10. Any
     *            default properties configured in the workflow file will be used when inputs are omitted.
     * @throws IOException
     *             the io exception
     */
    public void dispatch(String ref, Map<String, Object> inputs) throws IOException {
        Requester requester = root().createRequest()
                .method("POST")
                .withUrlPath(getApiRoute(), "dispatches")
                .with("ref", ref);

        if (!inputs.isEmpty()) {
            requester.with("inputs", inputs);
        }

        requester.send();
    }

    /**
     * Enable the workflow.
     *
     * @throws IOException
     *             the io exception
     */
    public void enable() throws IOException {
        root().createRequest().method("PUT").withUrlPath(getApiRoute(), "enable").send();
    }

    /**
     * The badge URL, like https://github.com/octo-org/octo-repo/workflows/CI/badge.svg
     *
     * @return the badge url
     */
    public URL getBadgeUrl() {
        return GitHubClient.parseURL(badgeUrl);
    }

    /**
     * Gets the html url.
     *
     * @return the html url
     */
    public URL getHtmlUrl() {
        return GitHubClient.parseURL(htmlUrl);
    }

    /**
     * The name of the workflow.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * The path of the workflow e.g. .github/workflows/blank.yaml
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Repository to which the workflow belongs.
     *
     * @return the repository
     */
    @SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "Expected behavior")
    public GHRepository getRepository() {
        return owner;
    }

    /**
     * The state of the workflow.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Lists the workflow runs belong to this workflow.
     *
     * @return the paged iterable
     */
    public PagedIterable<GHWorkflowRun> listRuns() {
        return new GHWorkflowRunsIterable(owner, root().createRequest().withUrlPath(getApiRoute(), "runs"));
    }

    private String getApiRoute() {
        if (owner == null) {
            // Workflow runs returned from search to do not have an owner. Attempt to use url.
            final URL url = Objects.requireNonNull(getUrl(), "Missing instance URL!");
            return StringUtils.prependIfMissing(url.toString().replace(root().getApiUrl(), ""), "/");

        }
        return "/repos/" + owner.getOwnerName() + "/" + owner.getName() + "/actions/workflows/" + getId();
    }

    /**
     * Wrap up.
     *
     * @param owner
     *            the owner
     * @return the GH workflow
     */
    GHWorkflow wrapUp(GHRepository owner) {
        this.owner = owner;
        return this;
    }

}
