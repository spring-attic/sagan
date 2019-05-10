package sagan.renderer.guides;

import java.io.IOException;
import java.util.Arrays;

import org.asciidoctor.Asciidoctor;
import org.junit.Before;
import org.junit.Test;
import sagan.renderer.RendererProperties;
import sagan.renderer.github.GithubClient;
import sagan.renderer.guides.content.AsciidoctorGuideContentContributor;
import sagan.renderer.guides.content.ImagesGuideContentContributor;
import sagan.renderer.guides.content.PwsGuideContentContributor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GuideRendererTests {

	private GuideRenderer renderer;

	private GithubClient githubClient;

	private RendererProperties properties;

	@Before
	public void setup() {
		this.properties = new RendererProperties();
		this.githubClient = mock(GithubClient.class);
		this.renderer = new GuideRenderer(this.githubClient, this.properties,
				Arrays.asList(new AsciidoctorGuideContentContributor(Asciidoctor.Factory.create()),
						new ImagesGuideContentContributor(), new PwsGuideContentContributor()));
	}

	@Test
	public void renderAsciidoctorContent() throws Exception {
		given(this.githubClient.downloadRepositoryAsZipball("spring-guides", "gs-sample"))
				.willReturn(readAsBytes("gs-sample.zip"));
		GuideContentResource result = this.renderer.render(GuideType.GETTING_STARTED, "sample");
		assertThat(result.getName()).isEqualTo("sample");
		assertThat(result.getContent()).contains("<p>This is a sample guide.</p>")
				.contains("<!-- rendered by Sagan Renderer Service -->");
		assertThat(result.getTableOfContents()).contains("<li><a href=\"#_sample_guide_title\">Sample Guide title</a></li>");
	}

	@Test
	public void renderImages() throws Exception {
		given(this.githubClient.downloadRepositoryAsZipball("spring-guides", "gs-sample"))
				.willReturn(readAsBytes("gs-sample.zip"));
		GuideContentResource result = this.renderer.render(GuideType.GETTING_STARTED, "sample");
		assertThat(result.getName()).isEqualTo("sample");
		assertThat(result.getImages())
				.anySatisfy(image -> {
					assertThat(image.getName()).isEqualTo("spring.svg");
					assertThat(image.getEncodedContent()).contains("PHN2Z");
				})
				.anySatisfy(image -> {
					assertThat(image.getName()).isEqualTo("guides.png");
					assertThat(image.getEncodedContent()).contains("iVBOR");
				});
	}

	@Test
	public void renderSampleGuideWithPwsMetadata() throws Exception {
		given(this.githubClient.downloadRepositoryAsZipball("spring-guides", "gs-sample"))
				.willReturn(readAsBytes("gs-sample-pws.zip"));
		GuideContentResource result = this.renderer.render(GuideType.GETTING_STARTED, "sample");
		assertThat(result.getName()).isEqualTo("sample");
		assertThat(result.getContent()).contains("<p>This is a sample guide.</p>")
				.contains("<!-- rendered by Sagan Renderer Service -->");
		assertThat(result.getTableOfContents()).contains("<li><a href=\"#_sample_guide_title\">Sample Guide title</a></li>");
		assertThat(result.getPushToPwsMetadata())
				.contains("repository: https://github.com/spring-guides/gs-rest-service.git")
				.contains("directory: complete")
				.contains("path: /greeting");
	}

	private byte[] readAsBytes(String path) throws IOException {
		ClassPathResource resource = new ClassPathResource(path, getClass());
		return StreamUtils.copyToByteArray(resource.getInputStream());
	}
}
