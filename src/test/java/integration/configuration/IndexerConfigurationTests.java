/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package integration.configuration;

import io.spring.site.indexer.configuration.IndexerConfiguration;

import org.junit.After;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import utils.FreePortFinder;

import static org.junit.Assert.assertNotNull;

public class IndexerConfigurationTests {

	private ConfigurableApplicationContext context;

	@After
	public void clean() {
		if (this.context != null) {
			this.context.close();
		}
	}

	@Test
	public void testContextLoading() throws Exception {
		int port = FreePortFinder.find();

		this.context = (ConfigurableApplicationContext) SpringApplication.run(
				IndexerConfiguration.class, "--server.port=" + port,
				"--spring.database.url=jdbc:hsqldb:mem:acceptancetestdb",
				"--search.indexer.delay=6000000",
				"--elasticsearch.client.endpoint=http://localhost:9200",
				"--elasticsearch.client.index=sagan-test",
				"--spring.profiles.active=integration-test");

		IndexerConfiguration configuration = this.context
				.getBean(IndexerConfiguration.class);
		assertNotNull(configuration);
		this.context.close();
	}

}
