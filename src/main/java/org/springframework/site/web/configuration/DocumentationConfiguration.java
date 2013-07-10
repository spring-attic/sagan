package org.springframework.site.web.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.bind.RelaxedDataBinder;
import org.springframework.bootstrap.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.site.domain.documentation.DocumentationService;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Properties;

@Configuration
@ComponentScan("org.springframework.site.domain.documentation")
public class DocumentationConfiguration {

	private static final Log logger = LogFactory.getLog(DocumentationConfiguration.class);

	@Autowired
	private DocumentationService documentationService;

	@PostConstruct
	public void loadDocumentationProjects() {
		bind("documentation.yml", documentationService);
	}

	public static void bind(String path,
							DocumentationService documentationService) {
		RelaxedDataBinder binder = new RelaxedDataBinder(documentationService);
		YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
		factory.setResources(new Resource[]{new ClassPathResource(path)});
		Properties properties = factory.getObject();
		logger.info("Binding properties: " + properties);
		properties.remove("projects");
		for (Object key : Collections.list(properties.propertyNames())) {
			if (key.toString().endsWith(("ersions"))) {
				properties.remove(key);
			}
		}
		binder.bind(new MutablePropertyValues(properties));
		Assert.state(!binder.getBindingResult().hasErrors(), "Errors binding "
				+ path + ": " + binder.getBindingResult().getAllErrors());
	}

}
