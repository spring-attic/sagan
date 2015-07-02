package sagan.projects.support;

import org.jsoup.nodes.Document;
import sagan.projects.Project;
import sagan.search.SearchEntryMapper;
import sagan.search.types.ProjectPage;

public class GithubPagesSearchEntryMapper implements SearchEntryMapper<Document> {

    private final Project project;

    public GithubPagesSearchEntryMapper(Project project) {
        this.project = project;
    }

    @Override
    public ProjectPage map(Document document) {
        ProjectPage entry = new ProjectPage();
        String text = document.getElementsByClass("body--container").text();
        entry.setRawContent(text);
        entry.setSummary(text.substring(0, Math.min(500, text.length())));
        entry.setTitle(document.title());
        entry.setPath(document.baseUri());
        entry.addFacetPaths("Projects", "Projects/Homepage", "Projects/" + project.getName());
        return entry;
    }

}
