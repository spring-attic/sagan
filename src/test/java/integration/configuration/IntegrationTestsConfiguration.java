package integration.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@Configuration
@Import({ApplicationConfiguration.class})
@ComponentScan("integration.stubs")
public class IntegrationTestsConfiguration {

	public static final GettingStartedGuide GETTING_STARTED_GUIDE =
			new GettingStartedGuide("gs-awesome-guide", "awesome-guide", "Awesome Guide :: Awesome getting started guide that isn't helpful", "Learn awesome stuff with this guide.", "Related resources");

	@Primary
	@Bean
	public RestTemplate mockRestTemplate() {
		return mock(RestTemplate.class);
	}

	@Primary
	@Bean
	public GettingStartedService offlineGettingStartedService() {
		return new GettingStartedService() {

			@Override
			public GettingStartedGuide loadGuide(String guideId) {
				return GETTING_STARTED_GUIDE;
			}

			@Override
			public List<GettingStartedGuide> listGuides() {
				ObjectMapper mapper = new ObjectMapper();
				try {
					String reposJson = "/org/springframework/site/domain/guides/springframework-meta.repos.offline.json";
					InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
					List<GuideRepo> guideRepos = mapper.readValue(json, new TypeReference<List<GuideRepo>>() {});
					return mapGuideReposToGettingStartedGuides(guideRepos);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			private List<GettingStartedGuide> mapGuideReposToGettingStartedGuides(List<GuideRepo> guideRepos) {
				List<GettingStartedGuide> guides = new ArrayList<>();
				for (GuideRepo githubRepo : guideRepos) {
					if (githubRepo.isGettingStartedGuide()) {
						guides.add(new GettingStartedGuide(githubRepo.getName(), githubRepo.getGuideId(), githubRepo.getDescription(), null, null));
					}
				}
				return guides;
			}

			@Override
			public byte[] loadImage(String guideSlug, String imageName) {
				return new byte[0];
			}
		};
	}

}
