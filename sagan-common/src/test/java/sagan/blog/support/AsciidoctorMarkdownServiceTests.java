package sagan.blog.support;

import org.asciidoctor.Asciidoctor;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AsciidoctorMarkdownServiceTests {

    private AsciidoctorMarkdownService service;

    @Before
    public void setup() {
        service = new AsciidoctorMarkdownService(Asciidoctor.Factory.create());
    }

    @Test
    public void renderLink() throws Exception {
        String markdown = "http://spring.io[my link]";
        String rendered = service.renderToHtml(markdown);
        Assert.assertThat(rendered, CoreMatchers.containsString("<p><a href=\"http://spring.io\">my link</a></p>"));
    }

    @Test
    public void renderFencedCodeBlock() throws Exception {
        String markdown = "```java\n" +
                "public static void main(String[] args) {}\n" +
                "```";
        String rendered = service.renderToHtml(markdown);
        Assert.assertThat(rendered, CoreMatchers.containsString("<pre class=\"prettyprint highlight\">"));
        Assert.assertThat(rendered, CoreMatchers.containsString("<code class=\"language-java\""));
    }

    @Test
    public void renderTitleAnchors() throws Exception {
        String markdown = "### This is a title";
        Assert.assertThat(
                service.renderToHtml(markdown), CoreMatchers.containsString(
                        "<h3 id=\"this-is-a-title\"><a class=\"anchor\" href=\"#this-is-a-title\"></a>This is a title</h3>"));
    }

    @Test
    public void renderAdmonition() throws Exception {
        String markdown = "NOTE: this is a note";
        String rendered = service.renderToHtml(markdown);
        Assert.assertThat(rendered, CoreMatchers.containsString("<div class=\"admonitionblock note\">"));
        Assert.assertThat(rendered, CoreMatchers.containsString("<div class=\"title\">Note</div>"));
        Assert.assertThat(rendered, CoreMatchers.containsString("this is a note"));
    }

}
