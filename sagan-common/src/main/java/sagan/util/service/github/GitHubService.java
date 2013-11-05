package sagan.util.service.github;

import sagan.util.service.MarkdownService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GitHubService implements MarkdownService {

    public static final String API_URL_BASE = "https://api.github.com";
    private final GitHubRestClient gitHubRestClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Log log = LogFactory.getLog(GitHubService.class);
    private static final String REPO_CONTENTS_PATH = "/repos/spring-guides/{repoId}/contents";
    private static final String GUIDE_IMAGES_PATH = REPO_CONTENTS_PATH + "/images/{imageName}";

    @Autowired
    public GitHubService(GitHubRestClient gitHubRestClient) {
        this.gitHubRestClient = gitHubRestClient;
    }

    /**
     * Process the given markdown through GitHub's Markdown Rendering API. Note that this
     * approach is used only for rendering blog posts; guides on the other hand are
     * fetched directly as HTML from GitHub using the Accept: header.
     *
     * See http://developer.github.com/v3/markdown
     */
    @Override
    public String renderToHtml(String markdownSource) {
        return gitHubRestClient.sendPostRequestForHtml("/markdown/raw", markdownSource);
    }

    public Readme getReadme(String path) {
        String response = gitHubRestClient.sendRequestForJson(path);

        try {
            return objectMapper.readValue(response, Readme.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAsciiDocFileAsHtml(String path) {
        String content = null;

        byte[] download = gitHubRestClient.sendRequestForDownload(path);

        String tempFilePrefix = path.replace("/", "-");
        try {
            // First, write the downloaded stream of bytes into a file
            File zipball = File.createTempFile(tempFilePrefix, ".zip");
            zipball.deleteOnExit();
            FileOutputStream zipOut = new FileOutputStream(zipball);
            zipOut.write(download);
            zipOut.close();

            // Open the zip file and unpack it
            ZipFile zipFile = new ZipFile(zipball);
            File unzippedRoot = null;
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry entry = e.nextElement();
                if (entry.isDirectory()) {
                    File dir = new File(zipball.getParent() + File.separator + entry.getName());
                    dir.mkdir();
                    if (unzippedRoot == null) {
                        unzippedRoot = dir; // first directory is the root
                    }
                } else {
                    StreamUtils.copy(zipFile.getInputStream(entry),
                            new FileOutputStream(zipball.getParent() + File.separator + entry.getName()));
                }
            }

            // Process the unzipped guide through asciidoctor, rendering HTML content
            Asciidoctor asciidoctor = Asciidoctor.Factory.create();
            content = asciidoctor.renderFile(
                    new File(unzippedRoot.getAbsolutePath() + File.separator + "README.asc"),
                    OptionsBuilder.options().safe(SafeMode.SAFE));

            // Delete the zipball and the unpacked content
            FileSystemUtils.deleteRecursively(zipball);
            FileSystemUtils.deleteRecursively(unzippedRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create temp file for source: " + tempFilePrefix);
        }

        return content;
    }

    public String getMarkdownFileAsHtml(String path) {
        return gitHubRestClient.sendRequestForHtml(path);
    }

    public GitHubRepo getRepoInfo(String githubUsername, String repoName) {
        String response = gitHubRestClient.sendRequestForJson("/repos/{user}/{repo}", githubUsername, repoName);

        try {
            return objectMapper.readValue(response, GitHubRepo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GitHubRepo[] getGitHubRepos(String reposPath) {
        String response = gitHubRestClient.sendRequestForJson(reposPath);

        try {
            return objectMapper.readValue(response, GitHubRepo[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public byte[] getGuideImage(String repoName, String imageName) {
        String response = gitHubRestClient.sendRequestForJson(GUIDE_IMAGES_PATH, repoName, imageName);
        try {
            Map<String, String> json = objectMapper.readValue(response, Map.class);
            return Base64.decode(json.get("content").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RepoContent> getRepoContents(String repoId) {
        String jsonResponse = gitHubRestClient.sendRequestForJson(REPO_CONTENTS_PATH, repoId);
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<RepoContent>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNameForUser(String username) {
        String jsonResponse = gitHubRestClient.sendRequestForJson("/users/{user}", username);
        try {
            Map<String, String> map = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, String>>() {
            });
            return map.get("name");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
