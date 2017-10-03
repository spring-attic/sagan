package sagan.renderer.markdown;

import sagan.renderer.markdown.MarkdownController;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MarkdownControllerTests {

    private MarkdownController controller;

    @Before
    public void setup() {
        controller = new MarkdownController();
    }

    @Test
    public void renderLink() throws Exception {
        String markdown = "[my link](http://spring.io)";
        assertThat(controller.render(markdown)).startsWith("<p><a href=\"http://spring.io\">my link</a></p>");
    }

    @Test
    public void renderFencedCodeBlock() throws Exception {
        String markdown = "```java\n" +
                "public static void main(String[] args) {}\n" +
                "```";
        assertThat(controller.render(markdown)).startsWith(
                "<pre><code class=\"prettyprint java\">public static void main(String[] args) {}\n" +
                        "</code></pre>");
    }

    @Test
    public void renderJavaScript() throws Exception {
        String markdown = "<script>alert('hello');</script>";
        assertThat(controller.render(markdown)).startsWith(
                "<script>alert('hello');</script>");
    }

    @Test
    public void renderRawHtml() throws Exception {
        String markdown = "raw html<span>inline</span>";
        assertThat(controller.render(markdown)).startsWith("<p>raw html<span>inline</span></p>");
    }

    @Test
    public void renderTitleAnchors() throws Exception {
        String markdown = "### This is a title";
        assertThat(controller.render(markdown)).startsWith(
                "<h3><a href=\"#this-is-a-title\" class=\"anchor\" name=\"this-is-a-title\"></a>This is a title</h3>");
    }

}
