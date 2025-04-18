package org.kohsuke.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

// TODO: Auto-generated Javadoc
/**
 * The type GHProjectCard.
 *
 * @author Gunnar Skjold
 */
public class GHProjectCard extends GHObject {

    /**
     * Create default GHProjectCard instance
     */
    public GHProjectCard() {
    }

    private GHProject project;
    private GHProjectColumn column;

    private String note;
    private GHUser creator;
    private String contentUrl, projectUrl, columnUrl;
    private boolean archived;

    /**
     * Gets the html url.
     *
     * @return the html url
     */
    public URL getHtmlUrl() {
        return null;
    }

    /**
     * Wrap gh project card.
     *
     * @param root
     *            the root
     * @return the gh project card
     */
    GHProjectCard lateBind(GitHub root) {
        return this;
    }

    /**
     * Wrap gh project card.
     *
     * @param column
     *            the column
     * @return the gh project card
     */
    GHProjectCard lateBind(GHProjectColumn column) {
        this.column = column;
        this.project = column.project;
        return lateBind(column.root());
    }

    /**
     * Gets project.
     *
     * @return the project
     * @throws IOException
     *             the io exception
     */
    @SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "Expected behavior")
    public GHProject getProject() throws IOException {
        if (project == null) {
            try {
                project = root().createRequest().withUrlPath(getProjectUrl().getPath()).fetch(GHProject.class);
            } catch (FileNotFoundException e) {
            }
        }
        return project;
    }

    /**
     * Gets column.
     *
     * @return the column
     * @throws IOException
     *             the io exception
     */
    @SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "Expected behavior")
    public GHProjectColumn getColumn() throws IOException {
        if (column == null) {
            try {
                column = root().createRequest()
                        .withUrlPath(getColumnUrl().getPath())
                        .fetch(GHProjectColumn.class)
                        .lateBind(root());
            } catch (FileNotFoundException e) {
            }
        }
        return column;
    }

    /**
     * Gets content if present. Might be a {@link GHPullRequest} or a {@link GHIssue}.
     *
     * @return the content
     * @throws IOException
     *             the io exception
     */
    public GHIssue getContent() throws IOException {
        if (StringUtils.isEmpty(contentUrl))
            return null;
        try {
            if (contentUrl.contains("/pulls")) {
                return root().createRequest().withUrlPath(getContentUrl().getPath()).fetch(GHPullRequest.class);
            } else {
                return root().createRequest().withUrlPath(getContentUrl().getPath()).fetch(GHIssue.class);
            }
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets note.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Gets creator.
     *
     * @return the creator
     */
    @SuppressFBWarnings(value = { "EI_EXPOSE_REP" }, justification = "Expected behavior")
    public GHUser getCreator() {
        return creator;
    }

    /**
     * Gets content url.
     *
     * @return the content url
     */
    public URL getContentUrl() {
        return GitHubClient.parseURL(contentUrl);
    }

    /**
     * Gets project url.
     *
     * @return the project url
     */
    public URL getProjectUrl() {
        return GitHubClient.parseURL(projectUrl);
    }

    /**
     * Gets column url.
     *
     * @return the column url
     */
    public URL getColumnUrl() {
        return GitHubClient.parseURL(columnUrl);
    }

    /**
     * Is archived boolean.
     *
     * @return the boolean
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * Sets note.
     *
     * @param note
     *            the note
     * @throws IOException
     *             the io exception
     */
    public void setNote(String note) throws IOException {
        edit("note", note);
    }

    /**
     * Sets archived.
     *
     * @param archived
     *            the archived
     * @throws IOException
     *             the io exception
     */
    public void setArchived(boolean archived) throws IOException {
        edit("archived", archived);
    }

    private void edit(String key, Object value) throws IOException {
        root().createRequest().method("PATCH").with(key, value).withUrlPath(getApiRoute()).send();
    }

    /**
     * Gets api route.
     *
     * @return the api route
     */
    protected String getApiRoute() {
        return String.format("/projects/columns/cards/%d", getId());
    }

    /**
     * Delete.
     *
     * @throws IOException
     *             the io exception
     */
    public void delete() throws IOException {
        root().createRequest().method("DELETE").withUrlPath(getApiRoute()).send();
    }
}
