package org.kohsuke.github;

import org.junit.Test;
import org.kohsuke.github.GHIssueEvent.GHIssueEventConsumer;

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

public class GHIssueEventConsumerTest {

    private class MockGHIssueEvent extends GHIssueEvent {
        private final String event;

        MockGHIssueEvent(final String event) {
            this.event = event;
        }

        @Override
        public String getEvent() {
            return this.event;
        }
    }

    @Test
    public void milestoneEventConsumer() {
        final GHIssueEventConsumer consumer = mock(GHIssueEventConsumer.class);
        doCallRealMethod().when(consumer).dispatch(any(GHIssueEvent.class));
        new MockGHIssueEvent(MILESTONED).accept(consumer);
        verify(consumer, times(1)).milestoned(any(), any());
        new MockGHIssueEvent(DEMILESTONED).accept(consumer);
        verify(consumer, times(1)).demilestoned(any(), any());
    }

    @Test
    public void labelEventConsumer() {
        final GHIssueEventConsumer consumer = mock(GHIssueEventConsumer.class);
        doCallRealMethod().when(consumer).dispatch(any(GHIssueEvent.class));
        new MockGHIssueEvent(LABELED).accept(consumer);
        verify(consumer, times(1)).labeled(any(), any());
        new MockGHIssueEvent(UNLABELED).accept(consumer);
        verify(consumer, times(1)).unlabeled(any(), any());
    }

    @Test
    public void assignmentEventConsumer() {
        final GHIssueEventConsumer consumer = mock(GHIssueEventConsumer.class);
        doCallRealMethod().when(consumer).dispatch(any(GHIssueEvent.class));
        new MockGHIssueEvent(ASSIGNED).accept(consumer);
        verify(consumer, times(1)).assigned(any(), any());
        new MockGHIssueEvent(UNASSIGNED).accept(consumer);
        verify(consumer, times(1)).unassigned(any(), any());
    }
}
