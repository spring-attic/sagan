package sagan.blog.support;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PegdownMarkdownServiceTests {

    private PegdownMarkdownService service;

    @Before
    public void setup() {
        service = new PegdownMarkdownService();
    }

    @Test
    public void renderTitle() throws Exception {
        String markdown = "### Title";
        Assert.assertEquals("<h3>Title</h3>", service.renderToHtml(markdown));
    }

    @Test
    public void renderFencedCodeBlock() throws Exception {
        String markdown = "```java\n" +
                "public static void main(String[] args) {}\n" +
                "```";
        Assert.assertEquals("<pre><code class=\"prettyprint java\">public static void main(String[] args) {}\n" +
                "</code></pre>", service.renderToHtml(markdown));
    }

    @Test
    public void renderJavaScript() throws Exception {
        String markdown = "##Title\n" +
                "<script>alert('hello');</script>";
        Assert.assertEquals("<h2>Title</h2>\n" +
                "<script>alert('hello');</script>", service.renderToHtml(markdown));
    }

    @Test
    public void renderRawHtml() throws Exception {
        String markdown = "#Title\n" +
                "<p>raw html</p>";
        Assert.assertEquals("<h1>Title</h1>\n" +
                "<p>raw html</p>", service.renderToHtml(markdown));
    }

}
