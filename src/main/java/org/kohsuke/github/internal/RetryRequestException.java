package org.kohsuke.github.internal;

import org.kohsuke.github.connector.GitHubConnectorRequest;

import javax.annotation.CheckForNull;
import java.io.IOException;

public class RetryRequestException extends IOException {
    private final GitHubConnectorRequest connectorRequest;

    public RetryRequestException() {
        this(null);
    }

    public RetryRequestException(GitHubConnectorRequest connectorRequest) {
        this.connectorRequest = connectorRequest;
    }

    @CheckForNull
    public GitHubConnectorRequest connectorRequest() {
        return connectorRequest;
    }

}
