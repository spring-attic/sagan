package sagan.docs.search;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.search.SearchEntry;
import sagan.search.SearchEntryMapper;

import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

class ApiDocumentMapper implements SearchEntryMapper<Document> {

    private Project project;
    private final ProjectRelease version;

    public ApiDocumentMapper(Project project, ProjectRelease version) {
        this.project = project;
        this.version = version;
    }

    public SearchEntry map(Document document) {
        if (document.baseUri().endsWith("allclasses-frame.html"))
            return null;

        String apiContent;

        Elements blocks = document.select(".block");
        if (blocks.size() > 0) {
            apiContent = blocks.text();
        } else {
            apiContent = document.select("p").text();
        }

        SearchEntry entry = new SearchEntry();

        entry.setPublishAt(new Date(0L));
        entry.setRawContent(apiContent);
        entry.setSummary(apiContent.substring(0, Math.min(apiContent.length(), 500)));
        entry.setTitle(document.title());
        entry.setSubTitle(String.format("%s (%s API)", project.getName(), version.getVersion()));
        entry.setPath(document.baseUri());
        entry.setCurrent(version.isCurrent());
        entry.setType("apiDoc");
        entry.setVersion(version.getVersion());
        entry.setProjectId(project.getId());

        entry.addFacetPaths("Projects", "Projects/Api", "Projects/" + project.getName(), "Projects/"
                + project.getName() + "/" + version.getVersion());
        return entry;
    }
}
