package integration.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.guides.Guide;
import org.springframework.site.domain.guides.GuidesService;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

@Configuration
@Import({ApplicationConfiguration.class})
@ComponentScan("integration.stubs")
public class IntegrationTestsConfiguration {

	public static final Guide GETTING_STARTED_GUIDE =
			new Guide("gs-awesome-guide", "awesome-guide",
					"Awesome Guide :: Awesome getting started guide that isn't helpful",
					"Learn awesome stuff with this guide.", "Related resources");

	@Primary
	@Bean
	public RestTemplate mockRestTemplate() {
		return mock(RestTemplate.class);
	}

	@Primary
	@Bean
	public GuidesService offlineGettingStartedService() {
		return new GuidesService() {

			@Override
			public Guide loadGettingStartedGuide(String guideId) {
				return GETTING_STARTED_GUIDE;
			}

			@Override
			public Guide loadTutorial(String guideId) {
				return GETTING_STARTED_GUIDE;
			}

			@Override
			public List<Guide> listGettingStartedGuides() {
				ObjectMapper mapper = new ObjectMapper();
				try {
					String reposJson = "/org/springframework/site/domain/guides/springframework-meta.repos.offline.json";
					InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
					List<GitHubRepo> guideRepos = mapper.readValue(json, new TypeReference<List<GitHubRepo>>() {});
					return mapGuideReposToGettingStartedGuides(guideRepos, "gs-");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			private List<Guide> mapGuideReposToGettingStartedGuides(List<GitHubRepo> guideRepos, String prefix) {
				List<Guide> guides = new ArrayList<>();
				for (GitHubRepo githubRepo : guideRepos) {
					if (githubRepo.getName().startsWith(prefix)) {
						guides.add(new Guide(githubRepo.getName(), githubRepo.getName().replaceAll("^" + prefix, ""), githubRepo.getDescription(), null, null));
					}
				}
				return guides;
			}

			@Override
			public byte[] loadImage(String guideSlug, String imageName) {
				return new byte[0];
			}

			@Override
			public Guide loadTutorialPage(String tutorialId, int page) {
				return GETTING_STARTED_GUIDE;
			}

			@Override
			public List<Guide> listTutorials() {
				return Arrays.asList();
			}
		};
	}

}
