package org.kohsuke.github;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.Matchers.*;

// TODO: Auto-generated Javadoc
/**
 * The Class GHTreeBuilderTest.
 */
public class GHTreeBuilderTest extends AbstractGitHubWireMockTest {

    private static byte[] CONTENT_DATA1 = { 0x01, 0x02, 0x03 };

    private static byte[] CONTENT_DATA2 = { 0x04, 0x05, 0x06, 0x07 };

    private static String CONTENT_README = "Thanks for using our application!\n";
    private static String CONTENT_SCRIPT = "#!/bin/bash\necho Hello\n";

    private static String PATH_DATA1 = "data/val1.dat";
    private static String PATH_DATA2 = "data/val2.dat";

    private static String PATH_README = "doc/readme.txt";
    private static String PATH_SCRIPT = "app/run.sh";

    private static String REPO_NAME = "hub4j-test-org/GHTreeBuilderTest";
    private GHRef mainRef;

    private GHRepository repo;
    private GHTreeBuilder treeBuilder;
    /**
     * Create default GHTreeBuilderTest instance
     */
    public GHTreeBuilderTest() {
    }

    /**
     * Cleanup.
     *
     * @throws Exception
     *             the exception
     */
    @Before
    @After
    public void cleanup() throws Exception {
        if (mockGitHub.isUseProxy()) {
            repo = getNonRecordingGitHub().getRepository(REPO_NAME);
            Arrays.asList(PATH_SCRIPT, PATH_README, PATH_DATA1, PATH_DATA2).forEach(path -> {
                try {
                    GHContent content = repo.getFileContent(path);
                    if (content != null) {
                        content.delete("Cleanup");
                    }
                } catch (IOException e) {
                }
            });
        }
    }

    /**
     * Sets the up.
     *
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {
        repo = gitHub.getRepository(REPO_NAME);
        mainRef = repo.getRef("heads/main");
        String mainTreeSha = repo.getTreeRecursive("main", 1).getSha();
        treeBuilder = repo.createTree().baseTree(mainTreeSha);
    }

    /**
     * Test add.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testAdd() throws Exception {
        treeBuilder.add(PATH_SCRIPT, CONTENT_SCRIPT, true);
        treeBuilder.add(PATH_README, CONTENT_README, false);
        treeBuilder.add(PATH_DATA1, CONTENT_DATA1, false);
        treeBuilder.add(PATH_DATA2, CONTENT_DATA2, false);

        GHCommit commit = updateTree();

        assertThat(getFileSize(PATH_SCRIPT), equalTo((long) CONTENT_SCRIPT.length()));
        assertThat(getFileSize(PATH_README), equalTo((long) CONTENT_README.length()));
        assertThat(getFileSize(PATH_DATA1), equalTo((long) CONTENT_DATA1.length));
        assertThat(getFileSize(PATH_DATA2), equalTo((long) CONTENT_DATA2.length));

        assertThat(commit.getCommitShortInfo().getAuthor().getEmail(), equalTo("author@author.com"));
        assertThat(commit.getCommitShortInfo().getCommitter().getEmail(), equalTo("committer@committer.com"));

    }

    /**
     * Test delete.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testDelete() throws Exception {
        // add test tree
        treeBuilder.add(PATH_README, CONTENT_README, false);
        treeBuilder.add(PATH_DATA1, CONTENT_DATA1, false);

        GHCommit commit = updateTree();

        assertThat(getFileSize(PATH_README), equalTo((long) CONTENT_README.length()));
        assertThat(getFileSize(PATH_DATA1), equalTo((long) CONTENT_DATA1.length));

        assertThat(commit.getCommitShortInfo().getAuthor().getEmail(), equalTo("author@author.com"));
        assertThat(commit.getCommitShortInfo().getCommitter().getEmail(), equalTo("committer@committer.com"));

        // remove a file from tree
        mainRef = repo.getRef("heads/main");
        treeBuilder = repo.createTree().baseTree(commit.getTree().getSha());
        treeBuilder.delete(PATH_DATA1);

        GHCommit deleteCommit = updateTree();

        assertThat(getFileSize(PATH_README), equalTo((long) CONTENT_README.length()));

        assertThat(deleteCommit.getCommitShortInfo().getAuthor().getEmail(), equalTo("author@author.com"));
        assertThat(deleteCommit.getCommitShortInfo().getCommitter().getEmail(), equalTo("committer@committer.com"));

        try {
            getFileSize(PATH_DATA1);
            fail("File " + PATH_DATA1 + " should not exist");
        } catch (IOException e) {
            assertThat(e.getMessage(), stringContainsInOrder(PATH_DATA1, "Not Found"));
        }
    }

    private long getFileSize(String path) throws IOException {
        GHContent content = repo.getFileContent(path);
        if (content == null)
            throw new IOException("File not found: " + path);
        return content.getSize();
    }

    private GHCommit updateTree() throws IOException {
        String treeSha = treeBuilder.create().getSha();
        GHCommit commit = new GHCommitBuilder(repo).message("Add files")
                .tree(treeSha)
                .author("author", "author@author.com", new Date(1611433225969L))
                .committer("committer", "committer@committer.com", new Date(1611433225968L))
                .parent(mainRef.getObject().getSha())
                .create();

        String commitSha = commit.getSHA1();
        mainRef.updateTo(commitSha);
        return commit;
    }
}
