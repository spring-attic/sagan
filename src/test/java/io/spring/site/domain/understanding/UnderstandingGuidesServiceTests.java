package io.spring.site.domain.understanding;

import org.junit.Before;
import org.junit.Test;

import io.spring.site.domain.services.github.GitHubService;
import io.spring.site.domain.services.github.RepoContent;
import io.spring.site.domain.understanding.UnderstandingGuide;
import io.spring.site.domain.understanding.UnderstandingGuidesService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;

public class UnderstandingGuidesServiceTests {

	private ArrayList<RepoContent> repoContents = new ArrayList<>();
	private GitHubService gitHubService = mock(GitHubService.class);
	private UnderstandingGuidesService understandingGuidesService = new UnderstandingGuidesService(gitHubService);

	@Before
	public void setUp() {
		addRepoContent(repoContents, "foo", "foo", "dir");
		addRepoContent(repoContents, "rest", "rest", "dir");
		addRepoContent(repoContents, "README.src", "README.src", "file");
		addRepoContent(repoContents, "README.md", "README.md", "file");
		given(gitHubService.getRepoContents("understanding")).willReturn(repoContents);
	}

	@Test
	public void returnsOnlyDirContents() {
		List<UnderstandingGuide> guides = understandingGuidesService.getGuides();

		assertThat(guides.size(), equalTo(2));
		assertThat(guides.get(0).getSubject(), equalTo("foo"));
		assertThat(guides.get(1).getSubject(), equalTo("rest"));
	}

	@Test
	public void testGetsContentForGuide() throws Exception {
		given(gitHubService.getRawFileAsHtml(matches(".*foo.*README.*"))).willReturn("Understanding: foo!");
		given(gitHubService.getRawFileAsHtml(matches(".*rest.*README.*"))).willReturn("Understanding: rest");

		List<UnderstandingGuide> guides = understandingGuidesService.getGuides();

		assertThat(guides.get(0).getContent(), equalTo("Understanding: foo!"));
		assertThat(guides.get(1).getContent(), equalTo("Understanding: rest"));
	}

	@Test
	public void testGetsSidebarForGuide() throws Exception {
		given(gitHubService.getRawFileAsHtml(matches(".*foo.*SIDEBAR.*"))).willReturn("foo sidebar");
		given(gitHubService.getRawFileAsHtml(matches(".*rest.*SIDEBAR.*"))).willReturn("rest sidebar");

		List<UnderstandingGuide> guides = understandingGuidesService.getGuides();

		assertThat(guides.get(0).getSidebar(), equalTo("foo sidebar"));
		assertThat(guides.get(1).getSidebar(), equalTo("rest sidebar"));
	}

	private void addRepoContent(ArrayList<RepoContent> repoContents, String name, String path, String type) {
		RepoContent repoContent = new RepoContent();
		repoContent.setName(name);
		repoContent.setPath(path);
		repoContent.setType(type);
		repoContents.add(repoContent);
	}
}
