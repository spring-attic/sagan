package sagan.blog.support;

import sagan.support.github.GitHubClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMarkdownServiceTest {

    @Mock
    GitHubClient gitHub;

    private MarkdownService service;

    @Before
    public void setUp() throws Exception {
        service = new DefaultMarkdownService(gitHub);
    }

    @Test
    public void renderToHtml_sendsMarkdownToGithub_returnsHtml() {
        String response = "<h3>Title</h3>";

        given(gitHub.sendPostRequestForHtml(anyString(), eq("### Title"))).willReturn(response);

        assertThat(service.renderToHtml("### Title"), equalTo(response));
    }

}
