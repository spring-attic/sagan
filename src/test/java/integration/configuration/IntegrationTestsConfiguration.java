package integration.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.guides.GettingStartedGuide;
import org.springframework.site.domain.guides.GettingStartedService;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.web.configuration.ApplicationConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.mockito.Mockito.mock;

@Configuration
@Import({ApplicationConfiguration.class})
@ComponentScan("integration.stubs")
public class IntegrationTestsConfiguration {

	public static final GettingStartedGuide GETTING_STARTED_GUIDE =
			new GettingStartedGuide("awesome-guide", "Awesome Guide :: Awesome getting started guide that isn't helpful", "Learn awesome stuff with this guide.", "Related resources");

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
			public List<GuideRepo> listGuides() {
				ObjectMapper mapper = new ObjectMapper();
				try {
					String reposJson = "/org/springframework/site/domain/guides/springframework-meta.repos.offline.json";
					InputStream json = new ClassPathResource(reposJson, getClass()).getInputStream();
					return mapper.readValue(json, new TypeReference<List<GuideRepo>>() {
					});
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public byte[] loadImage(String guideSlug, String imageName) {
				return new byte[0];
			}
		};
	}

}
