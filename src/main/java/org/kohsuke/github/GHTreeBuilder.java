package org.kohsuke.github;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Builder pattern for creating a new tree. Based on https://developer.github.com/v3/git/trees/#create-a-tree
 */
public class GHTreeBuilder {
    private static class DeleteTreeEntry extends TreeEntry {
        /**
         * According to reference doc https://docs.github.com/en/rest/git/trees?apiVersion=2022-11-28#create-a-tree: if
         * sha value is null then the file will be deleted. That's why in this DTO sha is always {@literal null} and is
         * included to json.
         */
        @JsonInclude
        private final String sha = null;

        private DeleteTreeEntry(String path) {
            // The `mode` and `type` parameters are required by the API, but their values are ignored during delete.
            // Supply reasonable placeholders.
            super(path, "100644", "blob");
        }
    }
    // Issue #636: Create Tree no longer accepts null value in sha field
    @JsonInclude(Include.NON_NULL)
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    private static class TreeEntry {

        private String content;
        private final String mode;
        private final String path;
        private String sha;
        private final String type;

        private TreeEntry(String path, String mode, String type) {
            this.path = path;
            this.mode = mode;
            this.type = type;
        }
    }

    private final GHRepository repo;

    private final Requester req;

    private final List<TreeEntry> treeEntries = new ArrayList<TreeEntry>();

    /**
     * Instantiates a new GH tree builder.
     *
     * @param repo
     *            the repo
     */
    GHTreeBuilder(GHRepository repo) {
        this.repo = repo;
        req = repo.root().createRequest();
    }

    /**
     * Adds a new entry with the given text content to the tree.
     *
     * @param path
     *            the file path in the tree
     * @param content
     *            the file content as UTF-8 encoded string
     * @param executable
     *            true, if the file should be executable
     * @return this GHTreeBuilder
     */
    public GHTreeBuilder add(String path, String content, boolean executable) {
        return add(path, content.getBytes(StandardCharsets.UTF_8), executable);
    }

    /**
     * Adds a new entry with the given binary content to the tree.
     *
     * @param path
     *            the file path in the tree
     * @param content
     *            the file content as byte array
     * @param executable
     *            true, if the file should be executable
     * @return this GHTreeBuilder
     */
    public GHTreeBuilder add(String path, byte[] content, boolean executable) {
        try {
            String dataSha = repo.createBlob().binaryContent(content).create().getSha();
            return shaEntry(path, dataSha, executable);
        } catch (IOException e) {
            throw new GHException("Cannot create binary content of '" + path + "'", e);
        }
    }

    /**
     * Base tree gh tree builder.
     *
     * @param baseTree
     *            the SHA of tree you want to update with new data
     * @return the gh tree builder
     */
    public GHTreeBuilder baseTree(String baseTree) {
        req.with("base_tree", baseTree);
        return this;
    }

    /**
     * Creates a tree based on the parameters specified thus far.
     *
     * @return the gh tree
     * @throws IOException
     *             the io exception
     */
    public GHTree create() throws IOException {
        req.with("tree", treeEntries);
        return req.method("POST").withUrlPath(getApiTail()).fetch(GHTree.class).wrap(repo);
    }

    /**
     * Removes an entry with the given path from base tree.
     *
     * @param path
     *            the file path in the tree
     * @return this GHTreeBuilder
     */
    public GHTreeBuilder delete(String path) {
        TreeEntry entry = new DeleteTreeEntry(path);
        treeEntries.add(entry);
        return this;
    }

    /**
     * Specialized version of entry() for adding an existing blob referred by its SHA.
     *
     * @param path
     *            the path
     * @param sha
     *            the sha
     * @param executable
     *            the executable
     * @return the gh tree builder
     * @deprecated use {@link #add(String, String, boolean)} or {@link #add(String, byte[], boolean)} instead.
     */
    @Deprecated
    public GHTreeBuilder shaEntry(String path, String sha, boolean executable) {
        TreeEntry entry = new TreeEntry(path, executable ? "100755" : "100644", "blob");
        entry.sha = sha;
        treeEntries.add(entry);
        return this;
    }

    /**
     * Specialized version of entry() for adding an existing blob specified {@code content}.
     *
     * @param path
     *            the path
     * @param content
     *            the content
     * @param executable
     *            the executable
     * @return the gh tree builder
     * @deprecated use {@link #add(String, String, boolean)} or {@link #add(String, byte[], boolean)} instead.
     */
    @Deprecated
    public GHTreeBuilder textEntry(String path, String content, boolean executable) {
        TreeEntry entry = new TreeEntry(path, executable ? "100755" : "100644", "blob");
        entry.content = content;
        treeEntries.add(entry);
        return this;
    }

    private String getApiTail() {
        return String.format("/repos/%s/%s/git/trees", repo.getOwnerName(), repo.getName());
    }
}
