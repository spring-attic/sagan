package integration.search;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.searchbox.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.search.SearchException;
import org.springframework.site.search.SearchService;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SearchIndexSetup {

	private static Log logger = LogFactory.getLog(SearchService.class);
	private final JestClient jestClient;

	public SearchIndexSetup(JestClient jestClient) {
		this.jestClient = jestClient;
	}

	public void deleteIndex() {
		execute(new DeleteIndex.Builder(SearchService.INDEX).build());
	}

	public void createIndex() {
		CreateIndex.Builder builder = new CreateIndex.Builder(SearchService.INDEX);

		builder.settings(loadSettings());
		execute(builder.build());

		createMappings();
	}

	public void createMappings() {
		try {

			File mappingsDir = new ClassPathResource("/config/elasticsearch/mappings", getClass()).getFile();
			for (final File fileEntry : mappingsDir.listFiles()) {
				String filenameBase = fileEntry.getName().replaceAll("\\.json$", "");
				String mappingJson = StreamUtils.copyToString(new FileInputStream(fileEntry), Charset.forName("UTF-8"));
				PutMapping.Builder mapping = new PutMapping.Builder(SearchService.INDEX, filenameBase, mappingJson);
				execute(mapping.build());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, String> loadSettings() {
		Map<String, String> settings = new HashMap<String, String>();
		try {
			InputStream settingsStream = new ClassPathResource("/config/elasticsearch/settings.json", getClass()).getInputStream();
			JsonParser jsonParser = new JsonParser();
			JsonElement root = jsonParser.parse(new InputStreamReader(settingsStream, Charset.forName("UTF-8")));

			for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject().entrySet()) {
				settings.put(entry.getKey(), entry.getValue().getAsString());
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return settings;
	}

	private JestResult execute(Action action) {
		try {
			JestResult result = this.jestClient.execute(action);
			logger.debug(result.getJsonString());
			return result;
		} catch (Exception e) {
			throw new SearchException(e);
		}
	}

}