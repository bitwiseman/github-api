package org.kohsuke.github;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;

import java.io.File;
import java.io.IOException;

/**
 * @author Kohsuke Kawaguchi
 */
public abstract class AbstractGitHubApiWireMockCacheTest extends AbstractGitHubApiWireMockTest {

    private static final OkHttpClient baseClient = new OkHttpClient();

    protected GitHubBuilder getGitHubBuilder() {
        OkHttpClient.Builder clientBuilder = baseClient.newBuilder();

        File cacheDir = new File("target/cache/" + baseFilesClassPath + "/" +  githubApi.getMethodName());
        cacheDir.mkdirs();
        try {
            FileUtils.cleanDirectory(cacheDir);
        } catch (IOException e) {}
        Cache cache = new Cache(cacheDir, 100 * 1024L * 1024L);

        clientBuilder.cache(cache);

        return super.getGitHubBuilder()
            .withConnector(new OkHttpConnector(clientBuilder.build()));
    }
}
