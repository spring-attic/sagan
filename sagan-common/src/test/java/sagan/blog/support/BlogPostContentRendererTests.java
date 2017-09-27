package sagan.blog.support;

import sagan.blog.PostFormat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BlogPostContentRendererTests {

    private PostContentRenderer renderer;

    @Mock
    private MarkdownService markdownService;

    @Before
    public void setUp() throws Exception {
        renderer = new PostContentRenderer(markdownService, null);
    }

    @Test
    public void sendsContentToMarkdownRenderer() throws Exception {
        given(markdownService.renderToHtml("CONTENT")).willReturn("RENDERED CONTENT");

        assertThat(renderer.render("CONTENT", PostFormat.MARKDOWN), equalTo("RENDERED CONTENT"));
    }

    @Test
    public void rendersDecodedHtml() throws Exception {
        String encoded =
                "FIRST\n" + "<pre>!{iframe src=\"//www.youtube.com/embed/D6nJSyWB-xA\"}{/iframe}</pre>\n" + "SECOND\n"
                        + "<pre>!{iframe src=\"//www.youtube.com/embed/jplkJIHPGos\"}{/iframe}</pre>\n" + "END";

        String decoded =
                "FIRST\n" + "<iframe src=\"//www.youtube.com/embed/D6nJSyWB-xA\"></iframe>\n" + "SECOND\n"
                        + "<iframe src=\"//www.youtube.com/embed/jplkJIHPGos\"></iframe>\n" + "END";

        given(markdownService.renderToHtml(encoded)).willReturn(encoded);

        assertThat(renderer.render(encoded, PostFormat.MARKDOWN), equalTo(decoded));
    }

    @Test
    public void rendersCallouts() throws Exception {
        given(markdownService.renderToHtml("CONTENT")).willReturn("[callout title=Title]Callout body[/callout]");

        assertThat(renderer.render("CONTENT", PostFormat.MARKDOWN), equalTo("<div class=\"callout\">\n"
                + "<div class=\"callout-title\">Title</div>\n" + "Callout body\n" + "</div>"));
    }

    @Test
    public void rendersMultipleCallouts() throws Exception {
        given(markdownService.renderToHtml("CONTENT"))
                .willReturn(
                        "[callout title=Title]Callout body[/callout] other content [callout title=Other Title]Other Callout body[/callout]");

        assertThat(renderer.render("CONTENT", PostFormat.MARKDOWN), equalTo("<div class=\"callout\">\n"
                + "<div class=\"callout-title\">Title</div>\n" + "Callout body\n" + "</div>" + " other content "
                + "<div class=\"callout\">\n" + "<div class=\"callout-title\">Other Title</div>\n"
                + "Other Callout body\n" + "</div>"));
    }
}
