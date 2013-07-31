package org.springframework.site.indexer.mapper;

import org.jsoup.nodes.Document;
import org.springframework.site.domain.documentation.Project;
import org.springframework.site.domain.documentation.SupportedVersion;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchEntryMapper;

import java.util.Date;

public class ReferenceDocumentSearchEntryMapper implements SearchEntryMapper<Document> {

	private final Project project;
	private final SupportedVersion version;

	public ReferenceDocumentSearchEntryMapper(Project project, SupportedVersion version) {
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
		entry.setPath(document.baseUri());
		entry.setCurrent(version.isCurrent());
		entry.addFacetPaths("Documentation", "Documentation/Reference", "Projects", "Projects/" + project.getName(),
				"Projects/" + project.getName() + "/" + version.getFullVersion());

		return entry;
	}

}
