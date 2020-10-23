package sagan.site.guides;


import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import sagan.site.TestSecurityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Integration tests for {@link DocsWebhookController}.
 */
@WebMvcTest(value = DocsWebhookController.class, properties = "sagan.site.github.webhook-token=accesstoken")
@Import(TestSecurityConfig.class)
public class DocsWebhookControllerTests {

	@MockBean
	private Tutorials tutorials;

	@MockBean
	private GettingStartedGuides gettingStartedGuides;

	@MockBean
	private Topicals topicals;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void missingHeadersShouldBeRejected() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\": \"this is a test\""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

	@Test
	void invalidHmacSignatureShouldBeRejected() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("X-Hub-Signature", "sha1=wronghmacvalue")
				.header("X-GitHub-Event", "push")
				.content("{\"message\": \"this is a test\""))
				.andExpect(MockMvcResultMatchers.status().isForbidden())
				.andExpect(MockMvcResultMatchers.content().string("{ \"message\": \"Forbidden\" }"));
	}

	@Test
	void pingEventShouldHaveResponse() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("X-Hub-Signature", "sha1=9E629DCCF4472F600D048510354BE400B8EB25CB")
				.header("X-GitHub-Event", "ping")
				.content(getTestPayload("pingWebhook")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("{ \"message\": \"Successfully processed ping event\" }"));
	}

	@Test
	void invalidJsonPushEventShouldBeRejected() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("X-Hub-Signature", "sha1=5df34e8979dc9a831873a42c6e172546f6937190")
				.header("X-GitHub-Event", "push")
				.content("this is a test message"))
				.andExpect(MockMvcResultMatchers.status().isBadRequest())
				.andExpect(MockMvcResultMatchers.content().string("{ \"message\": \"Bad Request\" }"));
	}

	@Test
	void shouldEvictGuideFromCache() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("X-Hub-Signature", "sha1=848E37804A9EC374FE1B8596AB25B15E98928C98")
				.header("X-GitHub-Event", "push")
				.content(getTestPayload("pushGettingStarted")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("{ \"message\": \"Successfully processed update\" }"));
		verify(this.gettingStartedGuides, times(1)).evictFromCache("test-guide");
	}

	@Test
	void shouldEvictTutorialFromCache() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("X-Hub-Signature", "sha1=751B1641F223E44119DD3F4A8BBAE7680ABDEF45")
				.header("X-GitHub-Event", "push")
				.content(getTestPayload("pushTutorial")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("{ \"message\": \"Successfully processed update\" }"));
		verify(this.tutorials, times(1)).evictFromCache("test-guide");
	}

	@Test
	void shouldEvictTopicalFromCache() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/webhook/docs/guides")
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
				.header("X-Hub-Signature", "sha1=84EB536B625A88DEB20F608F4510DCE60E81ADCA")
				.header("X-GitHub-Event", "push")
				.content(getTestPayload("pushTopical")))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.content().string("{ \"message\": \"Successfully processed update\" }"));
		verify(this.topicals, times(1)).evictFromCache("test-guide");
	}


	private String getTestPayload(String fileName) throws Exception {
		ClassPathResource resource = new ClassPathResource(fileName + ".json", getClass());
		return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8)
				.replaceAll("[\\n|\\r]", "");
	}

}
