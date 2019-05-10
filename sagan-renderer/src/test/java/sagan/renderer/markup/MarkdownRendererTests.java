package sagan.renderer.markup;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MarkdownRenderer}
 */
public class MarkdownRendererTests {

	private MarkdownRenderer renderer = new MarkdownRenderer();

	@Test
	public void renderLink() throws Exception {
		String markdown = "[my link](http://spring.io)";
		assertThat(renderer.renderToHtml(markdown)).isEqualTo("<p><a href=\"http://spring.io\">my link</a></p>");
	}

	@Test
	public void renderFencedCodeBlock() throws Exception {
		String markdown = "```java\n" +
				"public static void main(String[] args) {}\n" +
				"```";
		assertThat(renderer.renderToHtml(markdown))
				.isEqualTo("<pre><code class=\"prettyprint java\">public static void main(String[] args) {}\n" +
						"</code></pre>");
	}

	@Test
	public void renderJavaScript() throws Exception {
		String markdown = "<script>alert('hello');</script>";
		assertThat(renderer.renderToHtml(markdown)).isEqualTo("<script>alert('hello');</script>");
	}

	@Test
	public void renderRawHtml() throws Exception {
		String markdown = "raw html<span>inline</span>";
		assertThat(renderer.renderToHtml(markdown)).isEqualTo("<p>raw html<span>inline</span></p>");
	}

	@Test
	public void renderTitleAnchors() throws Exception {
		String markdown = "### This is a title";
		assertThat(renderer.renderToHtml(markdown))
				.isEqualTo("<h3><a href=\"#this-is-a-title\" class=\"anchor\" name=\"this-is-a-title\"></a>This is a title</h3>");
	}

}