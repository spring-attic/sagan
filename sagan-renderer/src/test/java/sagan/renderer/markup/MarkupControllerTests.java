package sagan.renderer.markup;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link MarkupController}
 */
@RunWith(SpringRunner.class)
@WebMvcTest(MarkupController.class)
public class MarkupControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private AsciidoctorRenderer asciidoctor;

	@MockBean
	private MarkdownRenderer markdown;


	@Test
	public void noCompatibleRenderer() throws Exception {
		given(this.asciidoctor.canRender(any())).willReturn(false);
		given(this.markdown.canRender(any())).willReturn(false);
		this.mvc.perform(post("/documents").content("test").contentType(MediaType.TEXT_PLAIN))
				.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void renderMarkdown() throws Exception {
		given(this.asciidoctor.canRender(any())).willReturn(false);
		given(this.markdown.canRender(MediaType.TEXT_MARKDOWN)).willReturn(true);
		given(this.markdown.renderToHtml("test")).willReturn("rendered");
		this.mvc.perform(post("/documents").content("test").contentType(MediaType.TEXT_MARKDOWN))
				.andExpect(status().isOk())
				.andExpect(content().string("rendered\n<!-- rendered by Sagan Renderer Service -->"));
	}

}
