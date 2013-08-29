package io.spring.site.indexer.mapper;

import io.spring.site.domain.projects.ProjectReleaseBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import io.spring.site.domain.projects.Project;
import io.spring.site.domain.projects.ProjectRelease;
import io.spring.site.search.SearchEntry;

import java.util.Collections;

import static io.spring.site.domain.projects.ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

public class ReferenceDocumentSearchEntryMapperTests {

	private Project project = new Project("spring",
			"Spring Project",
			"http://www.example.com/repo/spring-framework",
			"http://www.example.com/spring-framework",
			Collections.<ProjectRelease>emptyList());

	private ProjectRelease version = new ProjectReleaseBuilder()
			.versionName("3.2.1.RELEASE")
			.releaseStatus(GENERAL_AVAILABILITY)
			.current(true).build();

	private ReferenceDocumentSearchEntryMapper mapper = new ReferenceDocumentSearchEntryMapper(project, version);
	private SearchEntry entry;

	@Before
	public void setUp() throws Exception {
		Document document = Jsoup.parse("<p>ref doc</p>");
		entry = mapper.map(document);
	}

	@Test
	public void facetPaths() {
		assertThat(entry.getFacetPaths(), contains("Projects", "Projects/Reference", "Projects/Spring Project", "Projects/Spring Project/3.2.1.RELEASE"));
	}

	@Test
	public void projectId() {
		assertThat(entry.getProjectId(), equalTo("spring"));
	}

	@Test
	public void version() {
		assertThat(entry.getVersion(), equalTo("3.2.1.RELEASE"));
	}

	@Test
	public void subTitle() {
		assertThat(entry.getSubTitle(), equalTo("Spring Project (3.2.1.RELEASE Reference)"));
	}

}
