package org.springframework.site.indexer.mapper;

import org.jsoup.nodes.Document;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.ProjectRelease;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class ReferenceDocumentSearchEntryMapper implements SearchEntryMapper<Document> {

	private final Project project;
	private final ProjectRelease version;

	public ReferenceDocumentSearchEntryMapper(Project project, ProjectRelease version) {
		this.project = project;
		this.version = version;
	}

	@Override
	public SearchEntry map(Document document) {
		SearchEntry entry = new SearchEntry();
		entry.setPublishAt(new Date(0L));
		String text = document.text();

		entry.setRawContent(text);
		entry.setSummary(text.substring(0, Math.min(500, text.length())));
		entry.setTitle(document.title());
		entry.setSubTitle(String.format("%s (%s Reference)", project.getName(), version.getFullName()));
		entry.setPath(document.baseUri());
		entry.setCurrent(version.isCurrent());
		entry.setProjectId(project.getId());
		entry.setVersion(version.getFullName());

		entry.addFacetPaths("Documentation", "Documentation/Reference", "Projects", "Projects/" + project.getName(),
				"Projects/" + project.getName() + "/" + version.getFullName());

		return entry;
	}

}
