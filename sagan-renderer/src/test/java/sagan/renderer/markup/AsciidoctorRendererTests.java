package sagan.renderer.markup;

import org.asciidoctor.Asciidoctor;
import org.junit.Before;
import org.junit.Test;

import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AsciidoctorRenderer}
 */
public class AsciidoctorRendererTests {


	private AsciidoctorRenderer converter;

	@Before
	public void setup() {
		converter = new AsciidoctorRenderer(Asciidoctor.Factory.create());
	}

	@Test
	public void renderLink() throws Exception {
		String markdown = "http://spring.io[my link]";
		String rendered = converter.renderToHtml(markdown);
		assertThat(rendered).contains("<p><a href=\"http://spring.io\">my link</a></p>");
	}

	@Test
	public void renderFencedCodeBlock() throws Exception {
		String markdown = "```java\n" +
				"public static void main(String[] args) {}\n" +
				"```";
		String rendered = converter.renderToHtml(markdown);
		assertThat(rendered).contains("<pre class=\"prettyprint highlight\">")
				.contains("<code class=\"language-java\"");
	}

	@Test
	public void renderTitleAnchors() throws Exception {
		String markdown = "### This is a title";
		assertThat(converter.renderToHtml(markdown))
				.contains("<h3 id=\"this-is-a-title\"><a class=\"anchor\" href=\"#this-is-a-title\"></a>This is a title</h3>");
	}

	@Test
	public void renderAdmonition() throws Exception {
		String markdown = "NOTE: this is a note";
		String rendered = converter.renderToHtml(markdown);
		assertThat(rendered).contains("<div class=\"admonitionblock note\">")
				.contains("<div class=\"title\">Note</div>")
				.contains("this is a note");
	}

	@Test
	public void canConvert() {
		assertThat(converter.canRender(MediaType.APPLICATION_JSON)).isFalse();
		assertThat(converter.canRender(MediaType.parseMediaType("text/asciidoc"))).isTrue();
	}
}