package integration.search;

import integration.configuration.ElasticsearchStubConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.site.guides.GettingStartedGuide;
import org.springframework.site.guides.GettingStartedService;
import org.springframework.site.guides.GuideRepo;
import org.springframework.site.search.SearchEntry;
import org.springframework.site.search.SearchService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ElasticsearchStubConfiguration.class, GettingStartedGuideIndexServiceTests.OfflineConfiguration.class})
public class GettingStartedGuideIndexServiceTests {

	public static final GettingStartedGuide GETTING_STARTED_GUIDE =
			new GettingStartedGuide("awesome-guide", "Awesome getting started guide that isn't helpful", "Related resources");

	@Configuration
	protected static class OfflineConfiguration {

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
					ArrayList<GuideRepo> repos = new ArrayList<GuideRepo>();
					GuideRepo repo = new GuideRepo();
					repo.setName("gs-awesome-guide");
					repos.add(repo);
					return repos;
				}

				@Override
				public byte[] loadImage(String guideSlug, String imageName) {
					return new byte[0];
				}
			};
		}

		@Primary
		@Bean
		public SearchService mockSearchService() {
			return mock(SearchService.class);
		}

	}

	@Autowired
	private SearchService searchService;

	@Test
	public void gettingStartedGuidesAreIndexedOnStartup() {
		verify(searchService).saveToIndex(any(SearchEntry.class));
	}

}