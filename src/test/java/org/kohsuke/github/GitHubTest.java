package org.kohsuke.github;

import com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.github.example.dataobject.ReadOnlyObjects;

import java.io.IOException;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.kohsuke.github.GHMarketplaceAccountType.ORGANIZATION;

// TODO: Auto-generated Javadoc
/**
 * Unit test for {@link GitHub}.
 */
public class GitHubTest extends AbstractGitHubWireMockTest {

    /**
     * Create default GitHubTest instance
     */
    public GitHubTest() {
    }

    /**
     * List users.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void listUsers() throws IOException {
        for (GHUser u : Iterables.limit(gitHub.listUsers(), 10)) {
            assert u.getName() != null;
            // System.out.println(u.getName());
        }
    }

    /**
     * Gets the repository.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void getRepository() throws IOException {
        GHRepository repo = gitHub.getRepository("hub4j/github-api");

        assertThat(repo.getFullName(), equalTo("hub4j/github-api"));

        GHRepository repo2 = gitHub.getRepositoryById(repo.getId());
        assertThat(repo2.getFullName(), equalTo("hub4j/github-api"));

        try {
            gitHub.getRepository("hub4j_github-api");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("Repository name must be in format owner/repo"));
        }

        try {
            gitHub.getRepository("hub4j/github/api");
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), equalTo("Repository name must be in format owner/repo"));
        }
    }

    /**
     * Gets the orgs.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void getOrgs() throws IOException {
        int iterations = 10;
        Set<Long> orgIds = new HashSet<Long>();
        for (GHOrganization org : Iterables.limit(gitHub.listOrganizations().withPageSize(2), iterations)) {
            orgIds.add(org.getId());
            // System.out.println(org.getName());
        }
        assertThat(orgIds.size(), equalTo(iterations));

        GHOrganization org = gitHub.getOrganization("hub4j");
        GHOrganization org2 = gitHub.getOrganization("hub4j");
        assertThat(org.getLogin(), equalTo("hub4j"));
        // caching
        assertThat(org, sameInstance(org2));

        gitHub.refreshCache();
        org2 = gitHub.getOrganization("hub4j");
        assertThat(org2.getLogin(), equalTo("hub4j"));
        // cache cleared
        assertThat(org, not(sameInstance(org2)));
    }

    /**
     * Verifies that the `type` field is correctly fetched when listing organizations.
     * <p>
     * Since the `type` field is not included by default in the list of organizations, this test ensures that calling
     * {@code getType()} retrieves the expected value.
     * </p>
     *
     * @throws IOException
     *             if an I/O error occurs while fetching the organizations.
     */
    @Test
    public void listOrganizationsFetchesType() throws IOException {
        String type = gitHub.listOrganizations().withPageSize(1).iterator().next().getType();
        assertThat(type, equalTo("Organization"));
    }

    /**
     * Search users.
     */
    @Test
    public void searchUsers() {
        PagedSearchIterable<GHUser> r = gitHub.searchUsers().q("tom").repos(">42").followers(">1000").list();
        GHUser u = r.iterator().next();
        // System.out.println(u.getName());
        assertThat(u.getId(), notNullValue());
        assertThat(r.getTotalCount(), greaterThan(0));
    }

    /**
     * Test list all repositories.
     */
    @Test
    public void testListAllRepositories() {
        Iterator<GHRepository> itr = gitHub.listAllPublicRepositories().iterator();
        for (int i = 0; i < 115; i++) {
            assertThat(itr.hasNext(), is(true));
            GHRepository r = itr.next();
            // System.out.println(r.getFullName());
            assertThat(r.getUrl(), notNullValue());
            assertThat(r.getId(), not(0L));
        }

        // ensure the iterator throws as expected
        try {
            itr.remove();
            fail();
        } catch (UnsupportedOperationException e) {
            assertThat(e, notNullValue());
        }
    }

    /**
     * Search content.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void searchContent() throws Exception {
        PagedSearchIterable<GHContent> r = gitHub.searchContent()
                .q("addClass")
                .in("file")
                .language("js")
                .repo("jquery/jquery")
                // ignored unless sort is also set
                .order(GHDirection.DESC)
                .list();
        GHContent c = r.iterator().next();
        assertThat(c.getGitUrl(), endsWith("/repositories/167174/git/blobs/796fbcc808ca15bbe771f8c9c1a7bab3388f6128"));
        assertThat(c.getHtmlUrl(),
                endsWith(
                        "https://github.com/jquery/jquery/blob/a684e6ba836f7c553968d7d026ed7941e1a612d8/src/attributes/classes.js"));

        // System.out.println(c.getName());
        assertThat(c.getDownloadUrl(), notNullValue());
        assertThat(c.getOwner(), notNullValue());
        assertThat(c.getOwner().getFullName(), equalTo("jquery/jquery"));
        assertThat(r.getTotalCount(), greaterThan(5));

        PagedSearchIterable<GHContent> r2 = gitHub.searchContent()
                .q("addClass")
                .in("file")
                .language("js")
                .repo("jquery/jquery")
                // resets query sort back to default
                .sort(GHContentSearchBuilder.Sort.INDEXED)
                .sort(GHContentSearchBuilder.Sort.BEST_MATCH)
                // ignored unless sort is also set to non-default
                .order(GHDirection.ASC)
                .list();

        GHContent c2 = r2.iterator().next();
        assertThat(c2.getPath(), equalTo(c.getPath()));
        assertThat(r2.getTotalCount(), equalTo(r.getTotalCount()));

        PagedSearchIterable<GHContent> r3 = gitHub.searchContent()
                .q("addClass")
                .in("file")
                .language("js")
                .repo("jquery/jquery")
                .sort(GHContentSearchBuilder.Sort.INDEXED)
                .order(GHDirection.ASC)
                .list();

        GHContent c3 = r3.iterator().next();
        assertThat(c3.getPath(), not(equalTo(c2.getPath())));
        assertThat(r3.getTotalCount(), equalTo(r2.getTotalCount()));

        GHContentSearchBuilder searchBuilder = gitHub.searchContent()
                .q("addClass")
                .in("file")
                .language("js")
                .repo("jquery/jquery")
                .sort(GHContentSearchBuilder.Sort.INDEXED)
                .order(GHDirection.DESC);

        PagedSearchIterable<GHContent> r4 = searchBuilder.list();

        GHContent c4 = r4.iterator().next();
        assertThat(c4.getPath(), not(equalTo(c2.getPath())));
        assertThat(c4.getPath(), not(equalTo(c3.getPath())));
        assertThat(r4.getTotalCount(), equalTo(r2.getTotalCount()));

        // Verify qualifier not allowed to be empty
        IllegalArgumentException e = Assert.assertThrows(IllegalArgumentException.class,
                () -> searchBuilder.q("", "not valid"));
        assertThat(e.getMessage(), equalTo("qualifier cannot be null or empty"));
    }

    /**
     * Search content with forks.
     */
    @Test
    public void searchContentWithForks() {
        final PagedSearchIterable<GHContent> results = gitHub.searchContent()
                .q("addClass")
                .language("js")
                .sort(GHContentSearchBuilder.Sort.INDEXED)
                .order(GHDirection.DESC)
                .fork(GHFork.PARENT_ONLY)
                .list();

        final PagedSearchIterable<GHContent> resultsWithForks = gitHub.searchContent()
                .q("addClass")
                .language("js")
                .sort(GHContentSearchBuilder.Sort.INDEXED)
                .order(GHDirection.DESC)
                .fork(GHFork.PARENT_AND_FORKS)
                .list();

        assertThat(results.getTotalCount(), lessThan(resultsWithForks.getTotalCount()));

        // Do not record these.
        // This will verify that the queries for the deprecated path are identical to the ones above.
        if (!mockGitHub.isTakeSnapshot()) {
            final PagedSearchIterable<GHContent> resultsDeprecated = gitHub.searchContent()
                    .q("addClass")
                    .language("js")
                    .sort(GHContentSearchBuilder.Sort.INDEXED)
                    .order(GHDirection.DESC)
                    .fork(GHFork.PARENT_ONLY)
                    .list();

            final PagedSearchIterable<GHContent> resultsWithForksDeprecated = gitHub.searchContent()
                    .q("addClass")
                    .language("js")
                    .sort(GHContentSearchBuilder.Sort.INDEXED)
                    .order(GHDirection.DESC)
                    .fork(GHFork.PARENT_AND_FORKS)
                    .list();

            assertThat(resultsDeprecated.getTotalCount(), equalTo(results.getTotalCount()));
            assertThat(resultsWithForksDeprecated.getTotalCount(), equalTo(resultsWithForks.getTotalCount()));
        }
    }

    /**
     * Test list my authorizations.
     */
    @Test
    public void testListMyAuthorizations() {
        PagedIterable<GHAuthorization> list = gitHub.listMyAuthorizations();

        for (GHAuthorization auth : list) {
            assertThat(auth.getAppName(), notNullValue());
        }
    }

    /**
     * Gets the meta.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void getMeta() throws IOException {
        GHMeta meta = gitHub.getMeta();
        assertThat(meta.isVerifiablePasswordAuthentication(), is(true));
        assertThat(meta.getSshKeyFingerprints().size(), equalTo(4));
        assertThat(meta.getSshKeys().size(), equalTo(3));
        assertThat(meta.getApi().size(), equalTo(19));
        assertThat(meta.getGit().size(), equalTo(36));
        assertThat(meta.getHooks().size(), equalTo(4));
        assertThat(meta.getImporter().size(), equalTo(3));
        assertThat(meta.getPages().size(), equalTo(6));
        assertThat(meta.getWeb().size(), equalTo(20));
        assertThat(meta.getPackages().size(), equalTo(25));
        assertThat(meta.getActions().size(), equalTo(1739));
        assertThat(meta.getDependabot().size(), equalTo(3));

        // Also test examples here
        Class[] examples = new Class[]{ ReadOnlyObjects.GHMetaPublic.class, ReadOnlyObjects.GHMetaPackage.class,
                ReadOnlyObjects.GHMetaGettersUnmodifiable.class, ReadOnlyObjects.GHMetaGettersFinal.class,
                ReadOnlyObjects.GHMetaGettersFinalCreator.class, };

        for (Class metaClass : examples) {
            ReadOnlyObjects.GHMetaExample metaExample = gitHub.createRequest()
                    .withUrlPath("/meta")
                    .fetch((Class<ReadOnlyObjects.GHMetaExample>) metaClass);
            assertThat(metaExample.isVerifiablePasswordAuthentication(), is(true));
            assertThat(metaExample.getApi().size(), equalTo(19));
            assertThat(metaExample.getGit().size(), equalTo(36));
            assertThat(metaExample.getHooks().size(), equalTo(4));
            assertThat(metaExample.getImporter().size(), equalTo(3));
            assertThat(metaExample.getPages().size(), equalTo(6));
            assertThat(metaExample.getWeb().size(), equalTo(20));
        }
    }

    /**
     * Gets the my marketplace purchases.
     *
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Test
    public void getMyMarketplacePurchases() throws IOException {
        List<GHMarketplaceUserPurchase> userPurchases = gitHub.getMyMarketplacePurchases().toList();
        assertThat(userPurchases.size(), equalTo(2));

        for (GHMarketplaceUserPurchase userPurchase : userPurchases) {
            assertThat(userPurchase.isOnFreeTrial(), is(false));
            assertThat(userPurchase.getFreeTrialEndsOn(), nullValue());
            assertThat(userPurchase.getBillingCycle(), equalTo("monthly"));
            assertThat(userPurchase.getNextBillingDate(),
                    equalTo(GitHubClient.parseInstant("2020-01-01T00:00:00.000+13:00")));
            assertThat(userPurchase.getUpdatedAt(),
                    equalTo(GitHubClient.parseInstant("2019-12-02T00:00:00.000+13:00")));

            GHMarketplacePlan plan = userPurchase.getPlan();
            // GHMarketplacePlan - Non-nullable fields
            assertThat(plan.getUrl(), notNullValue());
            assertThat(plan.getAccountsUrl(), notNullValue());
            assertThat(plan.getName(), notNullValue());
            assertThat(plan.getDescription(), notNullValue());
            assertThat(plan.getPriceModel(), notNullValue());
            assertThat(plan.getState(), notNullValue());

            // GHMarketplacePlan - primitive fields
            assertThat(plan.getId(), not(0L));
            assertThat(plan.getNumber(), not(0L));
            assertThat(plan.getMonthlyPriceInCents(), greaterThanOrEqualTo(0L));

            // GHMarketplacePlan - list
            assertThat(plan.getBullets().size(), equalTo(2));

            GHMarketplaceAccount account = userPurchase.getAccount();
            // GHMarketplaceAccount - Non-nullable fields
            assertThat(account.getLogin(), notNullValue());
            assertThat(account.getUrl(), notNullValue());
            assertThat(account.getType(), notNullValue());

            // GHMarketplaceAccount - primitive fields
            assertThat(account.getId(), not(0L));

            /* logical combination tests */
            // Rationale: organization_billing_email is only set when account type is ORGANIZATION.
            if (account.getType() == ORGANIZATION)
                assertThat(account.getOrganizationBillingEmail(), notNullValue());
            else
                assertThat(account.getOrganizationBillingEmail(), nullValue());
        }
    }

    /**
     * Gzip.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void gzip() throws Exception {

        GHOrganization org = gitHub.getOrganization(GITHUB_API_TEST_ORG);

        // getResponseHeaderFields is deprecated but we'll use it for testing.
        assertThat(org.getResponseHeaderFields(), notNullValue());

        // WireMock should automatically gzip all responses
        assertThat(org.getResponseHeaderFields().get("Content-Encoding").get(0), is("gzip"));
        assertThat(org.getResponseHeaderFields().get("Content-eNcoding").get(0), is("gzip"));
    }

    /**
     * Test header field name.
     *
     * @throws Exception
     *             the exception
     */
    @Test
    public void testHeaderFieldName() throws Exception {

        GHOrganization org = gitHub.getOrganization(GITHUB_API_TEST_ORG);

        // getResponseHeaderFields is deprecated but we'll use it for testing.
        assertThat(org.getResponseHeaderFields(), notNullValue());

        assertThat("Header field names must be case-insensitive",
                org.getResponseHeaderFields().containsKey("CacHe-ContrOl"));

        assertThat("KeySet from header fields should also be case-insensitive",
                org.getResponseHeaderFields().keySet().contains("CacHe-ControL"));
        assertThat(org.getResponseHeaderFields().get("cachE-cOntrol").get(0), is("private, max-age=60, s-maxage=60"));
    }

    /**
     * Test expect GitHub {@link ServiceDownException}
     *
     */
    @Test
    public void testCatchServiceDownException() {
        snapshotNotAllowed();
        try {
            GHRepository repo = gitHub.getRepository("hub4j-test-org/github-api");
            repo.getFileContent("ghcontent-ro/service-down");
            fail("Exception was expected");
        } catch (IOException e) {
            assertThat(e.getClass().getName(), equalToIgnoringCase(ServiceDownException.class.getName()));
        }
    }
}
