package org.kohsuke.github;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.infradna.tool.bridge_method_injector.WithBridgeMethods;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.kohsuke.github.connector.GitHubConnectorResponse;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;

// TODO: Auto-generated Javadoc
/**
 * Most (all?) domain objects in GitHub seems to have these 4 properties.
 */
@SuppressFBWarnings(value = { "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_FIELD", "NP_UNWRITTEN_FIELD" },
        justification = "JSON API")
public abstract class GHObject extends GitHubInteractiveObject {
    /**
     * Capture response HTTP headers on the state object.
     */
    protected transient Map<String, List<String>> responseHeaderFields;

    private String url;

    private long id;
    private String nodeId;
    private String createdAt;
    private String updatedAt;

    /**
     * Instantiates a new GH object.
     */
    GHObject() {
    }

    /**
     * Called by Jackson.
     *
     * @param connectorResponse
     *            the {@link GitHubConnectorResponse} to get headers from.
     */
    @JacksonInject
    protected void setResponseHeaderFields(@CheckForNull GitHubConnectorResponse connectorResponse) {
        if (connectorResponse != null) {
            responseHeaderFields = connectorResponse.allHeaders();
        }
    }

    /**
     * Returns the HTTP response headers given along with the state of this object.
     *
     * <p>
     * Some of the HTTP headers have nothing to do with the object, for example "Cache-Control" and others are different
     * depending on how this object was retrieved.
     * <p>
     * This method was added as a kind of hack to allow the caller to retrieve OAuth scopes and such. Use with caution.
     * The method might be removed in the future.
     *
     * @return a map of header names to value lists
     */
    @CheckForNull
    @Deprecated
    public Map<String, List<String>> getResponseHeaderFields() {
        return GitHubClient.unmodifiableMapOrNull(responseHeaderFields);
    }

    /**
     * When was this resource created?.
     *
     * @return date created
     * @throws IOException
     *             on error
     */
    @WithBridgeMethods(value = Date.class, adapterMethod = "instantToDate")
    public Instant getCreatedAt() throws IOException {
        return GitHubClient.parseInstant(createdAt);
    }

    /**
     * Gets url.
     *
     * @return API URL of this object.
     */
    public URL getUrl() {
        return GitHubClient.parseURL(url);
    }

    /**
     * When was this resource last updated?.
     *
     * @return updated date
     * @throws IOException
     *             on error
     */
    @WithBridgeMethods(value = Date.class, adapterMethod = "instantToDate")
    public Instant getUpdatedAt() throws IOException {
        return GitHubClient.parseInstant(updatedAt);
    }

    /**
     * Get Global node_id from Github object.
     *
     * @return Global Node ID.
     * @see <a href="https://developer.github.com/v4/guides/using-global-node-ids/">Using Global Node IDs</a>
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Gets id.
     *
     * @return Unique ID number of this resource.
     */
    public long getId() {
        return id;
    }

    /**
     * String representation to assist debugging and inspection. The output format of this string is not a committed
     * part of the API and is subject to change.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this, TOSTRING_STYLE, null, null, false, false) {
            @Override
            protected boolean accept(Field field) {
                return super.accept(field) && !field.isAnnotationPresent(SkipFromToString.class);
            }
        }.toString();
    }

    private static final ToStringStyle TOSTRING_STYLE = new ToStringStyle() {
        {
            this.setUseShortClassName(true);
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            // skip unimportant properties. '_' is a heuristics as important properties tend to have short names
            if (fieldName.contains("_"))
                return;
            // avoid recursing other GHObject
            if (value instanceof GHObject)
                return;
            // likewise no point in showing root
            if (value instanceof GitHub)
                return;

            super.append(buffer, fieldName, value, fullDetail);
        }
    };
}
