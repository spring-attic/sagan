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
package integration.search;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.ClientConstants;

import java.util.LinkedHashSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dave Syer
 * 
 */
@Configuration
public class JestConfiguration {

	@Value("${elasticsearch.cluster.nodes:localhost:9300}")
	private String clusterNodes = "localhost:9300";

	@Bean
	public ClientConfig clientConfig() {
		ClientConfig clientConfig = new ClientConfig();
		LinkedHashSet<String> servers = new LinkedHashSet<String>();
		for (String url : this.clusterNodes.split(",")) {
			if (!url.startsWith("http")) {
				url = "http://" + url;
			}
			if (url.endsWith("9300")) {
				url = url.replace("9300", "9200");
			}
			servers.add(url);
		}
		clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);
		clientConfig.getProperties().put(ClientConstants.IS_MULTI_THREADED, true);
		return clientConfig;
	}

	@Bean
	public JestClient jestClient() {
		JestClientFactory factory = new JestClientFactory();
		factory.setClientConfig(clientConfig());
		return factory.getObject();
	}

}
