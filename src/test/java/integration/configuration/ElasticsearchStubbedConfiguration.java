package integration.configuration;

import org.elasticsearch.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import static org.mockito.Mockito.mock;

@Configuration
public class ElasticsearchStubbedConfiguration {

	// Mock to prevent Elasticsearch from starting up (slowly)
	@Bean
	public Client elasticSearchClient() throws Exception {
		return mock(Client.class);
	}

	// Mock to provide dependency for controller and service layers
	@Bean
	public ElasticsearchOperations elasticsearchTemplate() throws Exception {
		return mock(ElasticsearchOperations.class);
	}

	@Configuration
	@Conditional(ElasticsearchStubCondition.class)
	protected static class ProfileConfiguration {
	}

	// Use a Condition to force eager loading while still providing access to Environment
	private static class ElasticsearchStubCondition implements Condition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ConfigurableEnvironment environment = (ConfigurableEnvironment) context.getEnvironment();
			environment.addActiveProfile("elasticsearchstub");
			return true;
		}
	}
}
