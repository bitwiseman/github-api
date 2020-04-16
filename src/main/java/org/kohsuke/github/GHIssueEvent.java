package org.kohsuke.github;

import java.util.Date;
import java.util.function.Consumer;

/**
 * The type GHIssueEvent.
 *
 * @see <a href="https://developer.github.com/v3/issues/events/">Github documentation for issue
 *     events</a>
 * @author Martin van Zijl
 */
public class GHIssueEvent implements Consumer<GHIssueEvent.GHIssueEventConsumer> {
  private GitHub root;

  private long id;
  private String node_id;
  private String url;
  private GHUser actor;
  private String event;
  private String commit_id;
  private String commit_url;
  private String created_at;
  private GHMilestone milestone;
  private GHLabel label;
  private GHUser assignee;

  private GHIssue issue;

  /**
   * Interface for consuming event specific attributes.
   */
  public interface GHIssueEventConsumer {
    String MILESTONED = "milestoned";
    String DEMILESTONED = "demilestoned";
    String LABELED = "labeled";
    String UNLABELED = "unlabeled";
    String ASSIGNED = "assigned";
    String UNASSIGNED = "unassigned";

    /**
     * Invoked for "demilestoned" events.
     *
     * @param {@link GHMilestone} that the issue was removed from
     */
    default void demilestoned(GHIssue issue, GHMilestone milestone) {}

    /**
     * Invoked for "milestoned" events.
     *
     * @param {@link GHMilestone} that the issue was added to
     */
    default void milestoned(GHIssue issue, GHMilestone milestone) {}

    /**
     * Invoked for "labeled" events.
     *
     * @param {@link GHLabel} that was added to the issue
     */
    default void labeled(GHIssue issue, GHLabel label) {}

    /**
     * Invoked for "unlabeled" events.
     *
     * @param {@link GHLabel} that was removed from the issue
     */
    default void unlabeled(GHIssue issue, GHLabel label) {}
    /**
     * Invoked for "unassigned" events.
     *
     * @param {@link GHUser} that was unassigned from the issue
     */
    default void unassigned(GHIssue issue, GHUser user) {}

    /**
     * Invoked for "assigned" events.
     *
     * @param {@link GHUser} that was assigned to the issue
     */
    default void assigned(GHIssue issue, GHUser assignee) {}

    /**
     * Dispatch the given {@link GHIssueEvent to the appropriate method.
     *
     * @param event
     */
    default void dispatch(final GHIssueEvent event) {
      switch (event.getEvent()) {
        case MILESTONED:
          milestoned(event.issue, event.milestone);
          break;
        case DEMILESTONED:
          demilestoned(event.issue, event.milestone);
          break;
        case LABELED:
          labeled(event.issue, event.label);
          break;
        case UNLABELED:
          unlabeled(event.issue, event.label);
          break;
        case ASSIGNED:
          assigned(event.issue, event.assignee);
          break;
        case UNASSIGNED:
          unassigned(event.issue, event.assignee);
          break;
        default:
          // Not supported
      }
    }
  }

  /**
   * Apply {@link GHIssueEventConsumer} to this {@link GHIssueEvent}.
   *
   * @param consumer
   */
  public void accept(final GHIssueEventConsumer consumer) {
    consumer.dispatch(this);
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Gets node id.
   *
   * @return the node id
   */
  public String getNodeId() {
    return node_id;
  }

  /**
   * Gets url.
   *
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets actor.
   *
   * @return the actor
   */
  public GHUser getActor() {
    return actor;
  }

  /**
   * Gets event.
   *
   * @return the event
   */
  public String getEvent() {
    return event;
  }

  /**
   * Gets commit id.
   *
   * @return the commit id
   */
  public String getCommitId() {
    return commit_id;
  }

  /**
   * Gets commit url.
   *
   * @return the commit url
   */
  public String getCommitUrl() {
    return commit_url;
  }

  /**
   * Gets created at.
   *
   * @return the created at
   */
  public Date getCreatedAt() {
    return GitHubClient.parseDate(created_at);
  }

  /**
   * Gets root.
   *
   * @return the root
   */
  public GitHub getRoot() {
    return root;
  }

  /**
   * Gets issue.
   *
   * @return the issue
   */
  public GHIssue getIssue() {
    return issue;
  }

  GHIssueEvent wrapUp(GitHub root) {
    this.root = root;
    return this;
  }

  GHIssueEvent wrapUp(GHIssue parent) {
    this.issue = parent;
    this.root = parent.root;
    return this;
  }

  @Override
  public String toString() {
    return String.format(
        "Issue %d was %s by %s on %s",
        getIssue().getNumber(), getEvent(), getActor().getLogin(), getCreatedAt().toString());
  }
}
