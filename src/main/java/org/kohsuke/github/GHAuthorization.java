package org.kohsuke.github;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Generated OAuth token.
 *
 * @author janinko
 * @see GitHub#createToken(Collection, String, String) GitHub#createToken(Collection, String, String)
 * @see <a href="http://developer.github.com/v3/oauth/#create-a-new-authorization">API documentation</a>
 */
public class GHAuthorization extends GHObject {

    /**
     * Create default GHAuthorization instance
     */
    public GHAuthorization() {
    }

    /** The Constant USER. */
    public static final String USER = "user";

    /** The Constant USER_EMAIL. */
    public static final String USER_EMAIL = "user:email";

    /** The Constant USER_FOLLOW. */
    public static final String USER_FOLLOW = "user:follow";

    /** The Constant PUBLIC_REPO. */
    public static final String PUBLIC_REPO = "public_repo";

    /** The Constant REPO. */
    public static final String REPO = "repo";

    /** The Constant REPO_STATUS. */
    public static final String REPO_STATUS = "repo:status";

    /** The Constant DELETE_REPO. */
    public static final String DELETE_REPO = "delete_repo";

    /** The Constant NOTIFICATIONS. */
    public static final String NOTIFICATIONS = "notifications";

    /** The Constant GIST. */
    public static final String GIST = "gist";

    /** The Constant READ_HOOK. */
    public static final String READ_HOOK = "read:repo_hook";

    /** The Constant WRITE_HOOK. */
    public static final String WRITE_HOOK = "write:repo_hook";

    /** The Constant AMIN_HOOK. */
    public static final String AMIN_HOOK = "admin:repo_hook";

    /** The Constant READ_ORG. */
    public static final String READ_ORG = "read:org";

    /** The Constant WRITE_ORG. */
    public static final String WRITE_ORG = "write:org";

    /** The Constant ADMIN_ORG. */
    public static final String ADMIN_ORG = "admin:org";

    /** The Constant READ_KEY. */
    public static final String READ_KEY = "read:public_key";

    /** The Constant WRITE_KEY. */
    public static final String WRITE_KEY = "write:public_key";

    /** The Constant ADMIN_KEY. */
    public static final String ADMIN_KEY = "admin:public_key";

    private List<String> scopes;
    private String token;
    private String tokenLastEight;
    private String hashedToken;
    private App app;
    private String note;
    private String noteUrl;
    private String fingerprint;
    // TODO add some user class for https://developer.github.com/v3/oauth_authorizations/#check-an-authorization ?
    // private GHUser user;

    /**
     * Gets scopes.
     *
     * @return the scopes
     */
    public List<String> getScopes() {
        return Collections.unmodifiableList(scopes);
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets token last eight.
     *
     * @return the token last eight
     */
    public String getTokenLastEight() {
        return tokenLastEight;
    }

    /**
     * Gets hashed token.
     *
     * @return the hashed token
     */
    public String getHashedToken() {
        return hashedToken;
    }

    /**
     * Gets app url.
     *
     * @return the app url
     */
    public URL getAppUrl() {
        return GitHubClient.parseURL(app.url);
    }

    /**
     * Gets app name.
     *
     * @return the app name
     */
    public String getAppName() {
        return app.name;
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
     * Gets note url.
     *
     * @return the note url
     */
    public URL getNoteUrl() {
        return GitHubClient.parseURL(noteUrl);
    }

    /**
     * Gets fingerprint.
     *
     * @return the fingerprint
     */
    public String getFingerprint() {
        return fingerprint;
    }

    @SuppressFBWarnings(value = { "UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD", "UWF_UNWRITTEN_FIELD" },
            justification = "JSON API")
    private static class App {
        private String url;
        private String name;
        // private String client_id; not yet used
    }
}
