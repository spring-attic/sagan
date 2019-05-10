package sagan.site.blog;

import org.junit.Test;
import org.junit.runner.RunWith;
import sagan.SiteProperties;
import sagan.blog.PostFormat;
import sagan.site.renderer.SaganRendererClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest({SaganRendererClient.class, SiteProperties.class, PostContentRenderer.class})
@TestPropertySource(properties = "sagan.site.renderer.service-url=https://example.com/")
public class BlogPostContentRendererTests {

	@Autowired
	private PostContentRenderer renderer;

	@Autowired
	private MockRestServiceServer server;

	@Test
	public void shouldRenderMarkdown() {
		this.server.expect(requestTo("https://example.com/"))
				.andRespond(withSuccess(getClassPathResource("root.json"), MediaTypes.HAL_JSON));
		this.server.expect(requestTo("/documents"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Content-Type", "text/markdown"))
				.andRespond(withSuccess("RENDERED", MediaType.TEXT_HTML));

		String result = this.renderer.render("TESTCONTENT", PostFormat.MARKDOWN);
		assertThat(result).isEqualTo("RENDERED");

	}

	@Test
	public void rendersDecodedHtml() {
		String encoded =
				"FIRST\n" + "<pre>!{iframe src=\"//www.youtube.com/embed/D6nJSyWB-xA\"}{/iframe}</pre>\n" + "SECOND\n"
						+ "<pre>!{iframe src=\"//www.youtube.com/embed/jplkJIHPGos\"}{/iframe}</pre>\n" + "END";
		String decoded =
				"FIRST\n" + "<iframe src=\"//www.youtube.com/embed/D6nJSyWB-xA\"></iframe>\n" + "SECOND\n"
						+ "<iframe src=\"//www.youtube.com/embed/jplkJIHPGos\"></iframe>\n" + "END";

		this.server.expect(requestTo("https://example.com/"))
				.andRespond(withSuccess(getClassPathResource("root.json"), MediaTypes.HAL_JSON));
		this.server.expect(requestTo("/documents"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Content-Type", "text/markdown"))
				.andRespond(withSuccess(encoded, MediaType.TEXT_HTML));

		assertThat(renderer.render(encoded, PostFormat.MARKDOWN)).isEqualTo(decoded);
	}

	@Test
	public void rendersCallouts() {
		this.server.expect(requestTo("https://example.com/"))
				.andRespond(withSuccess(getClassPathResource("root.json"), MediaTypes.HAL_JSON));
		this.server.expect(requestTo("/documents"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Content-Type", "text/markdown"))
				.andRespond(withSuccess("[callout title=Title]Callout body[/callout]", MediaType.TEXT_HTML));

		assertThat(renderer.render("CONTENT", PostFormat.MARKDOWN)).isEqualTo("<div class=\"callout\">\n"
				+ "<div class=\"callout-title\">Title</div>\n" + "Callout body\n" + "</div>");
	}

	@Test
	public void rendersMultipleCallouts() {
		this.server.expect(requestTo("https://example.com/"))
				.andRespond(withSuccess(getClassPathResource("root.json"), MediaTypes.HAL_JSON));
		this.server.expect(requestTo("/documents"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Content-Type", "text/markdown"))
				.andRespond(withSuccess("[callout title=Title]Callout body[/callout] other content [callout title=Other Title]Other Callout body[/callout]", MediaType.TEXT_HTML));

		assertThat(renderer.render("CONTENT", PostFormat.MARKDOWN)).isEqualTo("<div class=\"callout\">\n"
				+ "<div class=\"callout-title\">Title</div>\n" + "Callout body\n" + "</div>" + " other content "
				+ "<div class=\"callout\">\n" + "<div class=\"callout-title\">Other Title</div>\n"
				+ "Other Callout body\n" + "</div>");
	}

	@Test
	public void shouldRenderAsciidoc() {
		this.server.expect(requestTo("https://example.com/"))
				.andRespond(withSuccess(getClassPathResource("root.json"), MediaTypes.HAL_JSON));
		this.server.expect(requestTo("/documents"))
				.andExpect(method(HttpMethod.POST))
				.andExpect(header("Content-Type", "text/asciidoc"))
				.andRespond(withSuccess("RENDERED", MediaType.TEXT_HTML));

		String result = this.renderer.render("TESTCONTENT", PostFormat.ASCIIDOC);
		assertThat(result).isEqualTo("RENDERED");

	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, SaganRendererClient.class);
	}

}
