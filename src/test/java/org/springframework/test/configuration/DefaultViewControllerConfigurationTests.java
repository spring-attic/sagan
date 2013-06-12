package org.springframework.test.configuration;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.site.configuration.DefaultViewControllerConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.URL;

import static org.mockito.Mockito.*;

public class DefaultViewControllerConfigurationTests {

	private WebMvcConfigurerAdapter configurer;
	private ViewControllerRegistry controllerRegistry;
	private ViewControllerRegistration viewRegistration;
	private ResourcePatternResolver resourceResolver;

	@Before
	public void setup() throws Exception {
		UrlResource resource = new UrlResource(new URL("file://test/templates/pages"));

		resourceResolver = mock(ResourcePatternResolver.class);
		controllerRegistry = mock(ViewControllerRegistry.class);
		viewRegistration = mock(ViewControllerRegistration.class);

		when(resourceResolver.getResource("classpath:/templates/pages")).thenReturn(resource);
		when(controllerRegistry.addViewController(anyString())).thenReturn(viewRegistration);

		configurer = new DefaultViewControllerConfiguration();
		ReflectionTestUtils.setField(configurer, "resourceResolver", resourceResolver);
	}

	@Test
	public void mapsHtmlPageToUrls() throws Exception {
		Resource[] resources = {new UrlResource(new URL("file://test/templates/pages/guides.html"))};

		when(resourceResolver.getResources("classpath:/templates/pages/**/*.html")).thenReturn(resources);


		configurer.addViewControllers(controllerRegistry);

		verify(controllerRegistry).addViewController("/guides");
		verify(viewRegistration).setViewName("pages/guides");
	}

	@Test
	public void mapsIndexPageToUrls() throws Exception {
		Resource[] resources = {new UrlResource(new URL("file://test/templates/pages/index.html"))};

		when(resourceResolver.getResources("classpath:/templates/pages/**/*.html")).thenReturn(resources);


		configurer.addViewControllers(controllerRegistry);

		verify(controllerRegistry).addViewController("/");
		verify(viewRegistration).setViewName("pages/index");
	}

	@Test
	public void mapsIndexHtmlPageToUrls() throws Exception {
		Resource[] resources = {new UrlResource(new URL("file://test/templates/pages/guides/index.html"))};

		when(resourceResolver.getResources("classpath:/templates/pages/**/*.html")).thenReturn(resources);


		configurer.addViewControllers(controllerRegistry);

		verify(controllerRegistry).addViewController("/guides");
		verify(viewRegistration).setViewName("pages/guides/index");
	}

}
