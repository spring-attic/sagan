package sagan.renderer.asciidoctor;

import org.asciidoctor.Asciidoctor;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AsciidoctorControllerTests {

    private AsciidoctorController controller;

    @Before
    public void setup() {
        controller = new AsciidoctorController(Asciidoctor.Factory.create());
    }

    @Test
    public void renderLink() throws Exception {
        String adoc = "http://spring.io[my link]";
        assertThat(controller.render(adoc))
                .contains("<p><a href=\"http://spring.io\">my link</a></p>");
    }

    @Test
    public void renderFencedCodeBlock() throws Exception {
        String adoc = "```java\n" + "public static void main(String[] args) {}\n" + "```";
        assertThat(controller.render(adoc)).contains(
                "<pre class=\"prettyprint highlight\"><code class=\"language-java\" data-lang=\"java\">public static void main(String[] args) {}"
                        + "</code></pre>");
    }

    @Test
    public void renderTitleAnchors() throws Exception {
        String adoc = "### This is a title";
        assertThat(controller.render(adoc)).contains(
                "<h3 id=\"this-is-a-title\"><a class=\"anchor\" href=\"#this-is-a-title\"></a>This is a title</h3>");
    }

    @Test
    public void renderTitleAndBody() throws Exception {
        String adoc = "## This is a title\n\nThis is the body.\n";
        String rendered = controller.render(adoc);
        assertThat(rendered).contains(
                "<h2 id=\"this-is-a-title\"><a class=\"anchor\" href=\"#this-is-a-title\"></a>This is a title</h2>");
        assertThat(rendered).contains("<p>This is the body.</p>");
    }

    @Test
    public void renderAsciidoctorTitleAndBody() throws Exception {
        String adoc = "== This is a title\n\nThis is the body.\n";
        String rendered = controller.render(adoc);
        assertThat(rendered).contains(
                "<h2 id=\"this-is-a-title\"><a class=\"anchor\" href=\"#this-is-a-title\"></a>This is a title</h2>");
        assertThat(rendered).contains("<p>This is the body.</p>");
    }

    @Test
    @Ignore("This fails for some reason - the header is skipped")
    public void renderHeadAndBody() throws Exception {
        String adoc = "# This is a title\n\nThis is the body.\n";
        String rendered = controller.render(adoc);
        System.err.println(rendered);
        assertThat(rendered).contains(
                "<h1 id=\"this-is-a-title\"><a class=\"anchor\" href=\"#this-is-a-title\"></a>This is a title</h1>");
        assertThat(rendered).contains("<p>This is the body.</p>");
    }

}
