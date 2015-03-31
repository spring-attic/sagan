package sagan.guides.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.WordUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.internal.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;
import org.yaml.snakeyaml.Yaml;
import sagan.support.github.GitHubClient;
import sagan.support.github.Readme;
import sagan.support.github.RepoContent;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Repository representing the GitHub organization (or user account) that contains guide
 * repositories.
 */
@Component
class GuideOrganization {

    private static final String REPO_BASE_PATH = "/repos/{name}/{repo}";
    private static final String REPO_CONTENTS_PATH = REPO_BASE_PATH + "/contents";

    private final String type;
    private final GitHubClient gitHub;
    private final String name;
    private final ObjectMapper objectMapper;
    private final Asciidoctor asciidoctor;
    private final Yaml yaml = new Yaml();

    @Autowired
    public GuideOrganization(@Value("${github.guides.owner.name}") String name,
                             @Value("${github.guides.owner.type}") String type,
                             GitHubClient gitHub,
                             ObjectMapper objectMapper) {
        this.name = name;
        this.type = type;
        this.gitHub = gitHub;
        this.objectMapper = objectMapper;
        this.asciidoctor = Asciidoctor.Factory.create();
    }

    /**
     * The name of the GitHub organization or user that 'owns' guides.
     */
    public String getName() {
        return name;
    }

    public Readme getReadme(String path) {
        String json = gitHub.sendRequestForJson(path);

        try {
            return objectMapper.readValue(json, Readme.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AsciidocGuide getAsciidocGuide(String path) {
        final String htmlContent;
        final Map<String, List<String>> frontMatter;
        final HashSet<String> tags;
        final HashSet<String> projects;
        final String tableOfContents;
        final Map<String, String> understandingDocs;

        byte[] download = gitHub.sendRequestForDownload(path);

        String tempFilePrefix = path.replace("/", "-");
        try {
            // First, write the downloaded stream of bytes into a file
            File zipball = File.createTempFile(tempFilePrefix, ".zip");
            zipball.deleteOnExit();
            FileOutputStream zipOut = new FileOutputStream(zipball);
            zipOut.write(download);
            zipOut.close();

            // Open the zip file and unpack it
            File unzippedRoot;
            try (ZipFile zipFile = new ZipFile(zipball)) {
                unzippedRoot = null;
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
            }

            // Process the unzipped guide through asciidoctor, rendering HTML content
            Attributes attributes = new Attributes();
            attributes.setAllowUriRead(true);
            attributes.setSkipFrontMatter(true);
            File readmeAdocFile = new File(unzippedRoot.getAbsolutePath() + File.separator + "README.adoc");
            String asciidoc = IOUtils.readFull(new FileReader(readmeAdocFile));

            String html = asciidoctor.render(
                    asciidoc,
                    OptionsBuilder.options().safe(SafeMode.SAFE).attributes(attributes).headerFooter(true)
                        .baseDir(readmeAdocFile.getParentFile()));

            Document doc = Jsoup.parse(html);

            htmlContent = doc.select("#content").html();
            frontMatter = parseFrontMatter(readmeAdocFile);

            if (frontMatter.containsKey("tags")) {
                tags = new HashSet<>(frontMatter.get("tags"));
            } else {
                tags = new HashSet<>(Arrays.asList(new String[0]));
            }

            if (frontMatter.containsKey("projects")) {
                projects = new HashSet<>(frontMatter.get("projects"));
            } else {
                projects = new HashSet<>(Arrays.asList(new String[0]));
            }

            tableOfContents = findTableOfContents(doc);
            understandingDocs = findUnderstandingLinks(doc);

            // Delete the zipball and the unpacked content
            FileSystemUtils.deleteRecursively(zipball);
            FileSystemUtils.deleteRecursively(unzippedRoot);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create temp file for source: " + tempFilePrefix);
        }

        return new AsciidocGuide(htmlContent, tags, projects, tableOfContents, understandingDocs);
    }

    /**
     * Parse front-matter
     *
     * @param readme asciidoctor file
     * @return set of categories
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> parseFrontMatter(File readme) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(readme))) {
            String frontMatter = "";
            String line = reader.readLine();
            if (line.startsWith("---")) {
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("---")) {
                        break;
                    } else {
                        frontMatter += line + "\n";
                    }
                }
                return (Map<String, List<String>>) yaml.load(frontMatter);
            }
        }
        return new HashMap<>();
    }

    /**
     * Extract top level table-of-content entries, and discard lower level links
     *
     * @param doc
     * @return HTML of the top tier table of content entries
     */
    private String findTableOfContents(Document doc) {
        Elements toc = doc.select("div#toc > ul.sectlevel1");

        toc.select("li > ul.sectlevel2").forEach(subsection ->
            subsection.parent().remove()
        );

        toc.forEach(part ->
            part.select("a[href]").stream()
                .filter(anchor ->
                        doc.select(anchor.attr("href")).get(0).parent().classNames().stream()
                                .anyMatch(clazz -> clazz.startsWith("reveal")))
                .forEach(href -> href.parent().remove())
        );

        return toc.toString();
    }

    /**
     * Scan document for links to understanding docs
     *
     * @param doc
     * @return map of understanding links in the form of (href -> display text)
     */
    private Map<String, String> findUnderstandingLinks(Document doc) {
        Map<String, String> understandingDocs = new HashMap<>();
        Elements elements = doc.select("a[href]");
        for (Element element : elements) {
            String href = element.attr("href");
            if (!href.equals("") && href.toLowerCase().contains("understanding")) {
                understandingDocs.put(href, WordUtils.capitalize(element.text()));
            }
        }
        return understandingDocs;
    }

    public String getMarkdownFileAsHtml(String path) {
        return gitHub.sendRequestForHtml(path);
    }

    public GitHubRepo getRepoInfo(String repoName) {
        String json = gitHub.sendRequestForJson(REPO_BASE_PATH, name, repoName);

        try {
            return objectMapper.readValue(json, GitHubRepo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GitHubRepo[] findAllRepositories() {
        String json = gitHub.sendRequestForJson("/{type}/{name}/repos?per_page=100", type, name);

        try {
            return objectMapper.readValue(json, GitHubRepo[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<GitHubRepo> findRepositoriesByPrefix(String prefix) {
        List<GitHubRepo> matches = new ArrayList<>();
        for (GitHubRepo candidate : findAllRepositories()) {
            if (candidate.getName().startsWith(prefix)) {
                matches.add(candidate);
            }
        }
        return matches;
    }

    @SuppressWarnings("unchecked")
    public byte[] getGuideImage(String repoName, String imageName) {
        String json = gitHub.sendRequestForJson(REPO_CONTENTS_PATH + "/images/{image}", name, repoName, imageName);
        try {
            Map<String, String> map = objectMapper.readValue(json, Map.class);
            return Base64.decode(map.get("content").getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RepoContent> getRepoContents(String repoName) {
        String json = gitHub.sendRequestForJson(REPO_CONTENTS_PATH, name, repoName);
        try {
            return objectMapper.readValue(json, new TypeReference<List<RepoContent>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
