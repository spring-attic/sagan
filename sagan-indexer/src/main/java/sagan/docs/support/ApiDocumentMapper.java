package sagan.docs.support;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.search.SearchEntryMapper;
import sagan.search.types.ApiDoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ApiDocumentMapper implements SearchEntryMapper<Document> {

    private static final Pattern OLD_JDK_TITLE = Pattern.compile("(\\w+).*");
    private static final Pattern JDK_TITLE = Pattern.compile("\\w+\\s+(\\w+).*");
    private Project project;
    private final ProjectRelease version;

    public ApiDocumentMapper(Project project, ProjectRelease version) {
        this.project = project;
        this.version = version;
    }

    public ApiDoc map(Document document) {
        if (document.baseUri().endsWith("allclasses-frame.html"))
            return null;

        String apiContent;

        Elements blocks = document.select(".block");
        if (blocks.size() > 0) {
            apiContent = blocks.text();
        } else {
            apiContent = document.select("p").text();
        }
        Elements subTitle = document.select(".header .subTitle");

        ApiDoc entry = new ApiDoc();
        entry.setClassName(findClassName(document));
        if (subTitle.size() == 1) {
            entry.setPackageName(subTitle.text());
        }
        entry.setRawContent(apiContent);
        entry.setSummary(apiContent.substring(0, Math.min(apiContent.length(), 500)));
        entry.setTitle(document.title());
        entry.setSubTitle(String.format("%s (%s API)", project.getName(), version.getVersion()));
        entry.setPath(document.baseUri());
        entry.setCurrent(version.isCurrent());
        entry.setVersion(version.getVersion());
        entry.setProjectId(project.getId());

        entry.addFacetPaths("Projects", "Projects/Api", "Projects/" + project.getName(), "Projects/"
                + project.getName() + "/" + version.getVersion());
        return entry;
    }

    private String findClassName(Document document) {
        Elements title = document.select(".header .title");
        if (title.size() == 1) {
            Matcher matcher = JDK_TITLE.matcher(title.text());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        else {
            Matcher matcher = OLD_JDK_TITLE.matcher(document.title());
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return "";
    }
}
