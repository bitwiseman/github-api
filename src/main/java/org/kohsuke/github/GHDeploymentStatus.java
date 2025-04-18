package org.kohsuke.github;

import java.net.URL;
import java.util.Locale;

// TODO: Auto-generated Javadoc
/**
 * The type GHDeploymentStatus.
 */
public class GHDeploymentStatus extends GHObject {

    /**
     * Create default GHDeploymentStatus instance
     */
    public GHDeploymentStatus() {
    }

    private GHRepository owner;

    /** The creator. */
    protected GHUser creator;

    /** The state. */
    protected String state;

    /** The description. */
    protected String description;

    /** The target url. */
    protected String targetUrl;

    /** The log url. */
    protected String logUrl;

    /** The deployment url. */
    protected String deploymentUrl;

    /** The repository url. */
    protected String repositoryUrl;

    /** The environment url. */
    protected String environmentUrl;

    /**
     * Wrap gh deployment status.
     *
     * @param owner
     *            the owner
     *
     * @return the gh deployment status
     */
    GHDeploymentStatus lateBind(GHRepository owner) {
        this.owner = owner;
        return this;
    }

    /**
     * Gets target url.
     *
     * @return the target url
     */
    public URL getLogUrl() {
        return GitHubClient.parseURL(logUrl);
    }

    /**
     * Gets deployment url.
     *
     * @return the deployment url
     */
    public URL getDeploymentUrl() {
        return GitHubClient.parseURL(deploymentUrl);
    }

    /**
     * Gets deployment environment url.
     *
     * @return the deployment environment url
     */
    public URL getEnvironmentUrl() {
        return GitHubClient.parseURL(environmentUrl);
    }

    /**
     * Gets repository url.
     *
     * @return the repository url
     */
    public URL getRepositoryUrl() {
        return GitHubClient.parseURL(repositoryUrl);
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public GHDeploymentState getState() {
        return GHDeploymentState.valueOf(state.toUpperCase(Locale.ENGLISH));
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    // test only
    GHRepository getOwner() {
        return owner;
    }
}
