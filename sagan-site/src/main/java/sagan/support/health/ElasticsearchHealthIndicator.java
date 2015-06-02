package sagan.support.health;

import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health.Builder;

public class ElasticsearchHealthIndicator extends AbstractHealthIndicator {


	private final JestClient jestClient;

	public ElasticsearchHealthIndicator(JestClient jestClient) {
		this.jestClient = jestClient;
	}

	@Override
	protected void doHealthCheck(Builder builder) throws Exception {


		JestResult result = jestClient.execute(new io.searchbox.cluster.Health.Builder().build());
		JsonObject map = result.getJsonObject();
		switch(getProperty(map, "status")) {
			case "green":
			case "yellow":
				builder.up();
				break;
			case "red":
			default:
				builder.down();
				break;
		}
		builder.withDetail("cluster_status", getProperty(map, "status"));
		builder.withDetail("timed_out", getProperty(map, "timed_out"));
		builder.withDetail("number_of_nodes", getProperty(map, "number_of_nodes"));
		builder.withDetail("number_of_data_nodes", getProperty(map, "number_of_data_nodes"));
		builder.withDetail("active_primary_shards", getProperty(map, "active_primary_shards"));
		builder.withDetail("active_shards", getProperty(map, "active_shards"));
		builder.withDetail("relocating_shards", getProperty(map, "relocating_shards"));
		builder.withDetail("initializing_shards", getProperty(map, "initializing_shards"));
		builder.withDetail("unassigned_shards", getProperty(map, "unassigned_shards"));
		builder.withDetail("number_of_pending_tasks", getProperty(map, "number_of_pending_tasks"));

	}

	private String getProperty(JsonObject map, String prop) {
		return map.get(prop).getAsString();
	}
}
