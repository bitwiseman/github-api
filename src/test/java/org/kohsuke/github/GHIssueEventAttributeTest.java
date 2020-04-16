package org.kohsuke.github;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer.ASSIGNED;
import static org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer.DEMILESTONED;
import static org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer.LABELED;
import static org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer.MILESTONED;
import static org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer.UNASSIGNED;
import static org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer.UNLABELED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class GHIssueEventAttributeTest extends AbstractGitHubWireMockTest {

    @NotNull
    private List<GHIssueEvent> listEvents(final String... types) throws IOException {
        return StreamSupport
                .stream(gitHub.getRepository("chids/project-milestone-test").getIssue(1).listEvents().spliterator(),
                        false)
                .filter(e -> asList(types).contains(e.getEvent()))
                .collect(toList());
    }

    @Test
    public void milestoneEvents() throws IOException {
        final List<GHIssueEvent> events = listEvents(MILESTONED, DEMILESTONED);
        final GHIssueEventConsumer consumer = mock(GHIssueEventConsumer.class);
        doCallRealMethod().when(consumer).dispatch(any(GHIssueEvent.class));
        events.forEach(e -> e.accept(consumer));
        verify(consumer, times(2)).milestoned(any(GHIssue.class), any(GHMilestone.class));
        verify(consumer, times(1)).demilestoned(any(GHIssue.class), any(GHMilestone.class));
    }

    @Test
    public void labelEvents() throws IOException {
        final List<GHIssueEvent> events = listEvents(LABELED, UNLABELED);
        final GHIssueEventConsumer consumer = mock(GHIssueEventConsumer.class);
        doCallRealMethod().when(consumer).dispatch(any(GHIssueEvent.class));
        events.forEach(e -> e.accept(consumer));
        verify(consumer, times(1)).labeled(any(GHIssue.class), any(GHLabel.class));
        verify(consumer, times(1)).unlabeled(any(GHIssue.class), any(GHLabel.class));
    }

    @Test
    public void assignmentEvents() throws IOException {
        final List<GHIssueEvent> events = listEvents(ASSIGNED, UNASSIGNED);
        final GHIssueEventConsumer consumer = mock(GHIssueEventConsumer.class);
        doCallRealMethod().when(consumer).dispatch(any(GHIssueEvent.class));
        events.forEach(e -> e.accept(consumer));
        verify(consumer, times(1)).assigned(any(GHIssue.class), any(GHUser.class));
        verify(consumer, times(1)).unassigned(any(GHIssue.class), any(GHUser.class));
    }
}
