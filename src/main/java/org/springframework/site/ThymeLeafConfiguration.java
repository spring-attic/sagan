package org.springframework.site;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.bootstrap.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.bootstrap.context.annotation.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("dev")
public class ThymeLeafConfiguration {

	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${spring.template.prefix:classpath:/templates/}")
	private String prefix;

	@Value("${spring.template.suffix:.html}")
	private String suffix;

	@Value("${spring.template.mode:HTML5}")
	private String templateMode;

	@Bean
	public ITemplateResolver defaultTemplateResolver() {
		TemplateResolver resolver = new TemplateResolver();
		resolver.setResourceResolver(new IResourceResolver() {
			@Override
			public InputStream getResourceAsStream(
					TemplateProcessingParameters templateProcessingParameters,
					String resourceName) {
				try {
					return resourceLoader.getResource(resourceName).getInputStream();
				} catch (IOException e) {
					return null;
				}
			}

			@Override
			public String getName() {
				return "SPRING";
			}
		});
		resolver.setPrefix(this.prefix);
		resolver.setSuffix(this.suffix);
		resolver.setTemplateMode(this.templateMode);
		resolver.setCacheable(false);
		return resolver;
	}

}
