package integration.stubs;

import com.fasterxml.jackson.databind.ObjectMapper;
import integration.configuration.IntegrationTestsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.guides.GitHubGuidesService;
import org.springframework.site.domain.guides.Guide;
import org.springframework.site.test.FixtureLoader;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Service
@Primary
@SuppressWarnings("unused")
public class StubGuidesService extends GitHubGuidesService {

	@Autowired
	public StubGuidesService(StubGithubService githubService) {
		super(githubService);
	}

	@Override
	public Guide loadGettingStartedGuide(String guideId) {
		return IntegrationTestsConfiguration.GETTING_STARTED_GUIDE;
	}

	@Override
	public Guide loadTutorial(String guideId) {
		return IntegrationTestsConfiguration.GETTING_STARTED_GUIDE;
	}

	@Override
	public List<Guide> listGettingStartedGuides() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String reposJson = "/org/springframework/site/domain/guides/springframework-meta.repos.offline.json";
			InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
			GitHubRepo[] gitHubRepos = mapper.readValue(json, GitHubRepo[].class);
			return mapGuideReposToGettingStartedGuides(gitHubRepos, "gs-");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] loadGettingStartedImage(String guideSlug, String imageName) {
		return FixtureLoader.loadData("/fixtures/images/testImage.png");
	}

	@Override
	public Guide loadTutorialPage(String tutorialId, String pagePath) {
		return IntegrationTestsConfiguration.GETTING_STARTED_GUIDE;
	}

	@Override
	public byte[] loadTutorialImage(String tutorialId, String imageName) {
		return FixtureLoader.loadData("/fixtures/images/testImage.png");
	}

	@Override
	public List<Guide> listTutorials() {
		return Arrays.asList();
	}
}
