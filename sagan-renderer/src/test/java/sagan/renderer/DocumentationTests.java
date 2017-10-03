package sagan.renderer;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.cloud.contract.wiremock.restdocs.WireMockRestDocs.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureRestDocs(outputDir = "build/snippets", uriHost = "spring.io", uriPort = 80)
@AutoConfigureMockMvc
public class DocumentationTests {

    @Autowired
    private MockMvc mvc;

    @Test
    public void renderLink() throws Exception {
        String markdown = "[link](http://spring.io)";
        mvc.perform(post("/documents").content(markdown)).andExpect(status().isOk())
                .andExpect(content().string(
                        startsWith("<p><a href=\"http://spring.io\">link</a></p>")))
                .andDo(document("render"));
    }

    @Test
    public void renderMarkdownLink() throws Exception {
        String markdown = "[link](http://spring.io)";
        mvc.perform(post("/documents").content(markdown)
                .contentType(MediaType.valueOf("text/markdown")))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        startsWith("<p><a href=\"http://spring.io\">link</a></p>")))
                .andDo(document("markdown"));
    }

    @Test
    public void renderAsciiDoctorLink() throws Exception {
        String markdown = "http://spring.io[link]";
        mvc.perform(post("/documents").content(markdown)
                .contentType(MediaType.valueOf("text/asciidoc")))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("<div class=\"paragraph\">")))
                .andExpect(content().string(
                        containsString("<p><a href=\"http://spring.io\">link</a></p>")))
                .andDo(document("asciidoc"));
    }

    @Test
    public void exampleMarkdownBlog() throws Exception {
        String markdown = "# Sample blog\nThis sample is a Markdown blog.";
        mvc.perform(post("/documents").content(markdown)).andExpect(status().isOk())
                .andExpect(content().string(startsWith(
                        "<h1><a href=\"#sample-blog\" class=\"anchor\" name=\"sample-blog\"></a>Sample blog</h1>")))
                .andDo(verify()
                        .wiremock(WireMock.post(urlPathEqualTo("/documents"))
                                .withRequestBody(matching(".*")).atPriority(5))
                        .stub("markdown-blog"));
    }

    @Test
    public void exampleAsciidoctorBlog() throws Exception {
        String markdown = "## Sample blog\n\nThis sample is an Asciidoctor blog.";
        mvc.perform(post("/documents")
                .contentType(MediaType.valueOf("text/asciidoc")).content(markdown))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "<h2 id=\"sample-blog\"><a class=\"anchor\" href=\"#sample-blog\"></a>Sample blog</h2>")))
                .andDo(verify().wiremock(WireMock.post(urlPathEqualTo("/documents"))
                        .withRequestBody(matching(".*"))
                        .withHeader("Content-Type", new ContainsPattern("text/asciidoc"))
                        .atPriority(4)).stub("asciidoc-blog"));
    }

    @Test
    public void exampleGuide() throws Exception {
        mvc.perform(get("/guides/spring-guides/getting-started-guides"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"tableOfContents\":")))
                .andDo(verify()
                        .wiremock(
                                WireMock.get(urlPathMatching("/guides/spring-guides/.*")))
                        .stub("guide"));
    }

}
