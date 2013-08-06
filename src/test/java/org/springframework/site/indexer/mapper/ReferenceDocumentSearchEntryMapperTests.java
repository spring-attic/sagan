package org.springframework.site.indexer.mapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.ProjectVersion;
import org.springframework.site.search.SearchEntry;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class ReferenceDocumentSearchEntryMapperTests {

	private Project project = new Project("spring",
			"Spring",
			"http://www.example.com/repo/spring-framework",
			"http://www.example.com/spring-framework",
			Collections.<ProjectVersion>emptyList());

	private ProjectVersion version = new ProjectVersion("3.2.1.RELEASE", ProjectVersion.Release.CURRENT, "", "");
	private ReferenceDocumentSearchEntryMapper mapper = new ReferenceDocumentSearchEntryMapper(project, version);
	private SearchEntry entry;

	@Before
	public void setUp() throws Exception {
		Document document = Jsoup.parse("<p>ref doc</p>");
		entry = mapper.map(document);
	}

	@Test
	public void facetPaths() {
		assertThat(entry.getFacetPaths(), contains("Documentation", "Documentation/Reference", "Projects", "Projects/Spring", "Projects/Spring/3.2.1.RELEASE"));
	}

	@Test
	public void projectId() {
		assertThat(entry.getProjectId(), equalTo("spring"));
	}

	@Test
	public void version() {
		assertThat(entry.getVersion(), equalTo("3.2.1.RELEASE"));
	}

}
