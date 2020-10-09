package sagan.renderer.github;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RateLimit {

	private final Integer limit;

	private final Integer remaining;

	private final Instant reset;

	private final Integer used;

	@JsonCreator
	public RateLimit(@JsonProperty("rate") Map<String, String> rate) {
		this.limit = Integer.parseInt(rate.get("limit"));
		this.remaining = Integer.parseInt(rate.get("remaining"));
		this.reset = Instant.ofEpochSecond(Long.parseLong(rate.get("reset")));
		this.used = Integer.parseInt(rate.get("used"));
	}

	public Integer getLimit() {
		return this.limit;
	}

	public Integer getRemaining() {
		return this.remaining;
	}

	public Instant getReset() {
		return this.reset;
	}

	public Integer getUsed() {
		return this.used;
	}

	public Map<String, Object> asMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("limit", this.limit);
		map.put("remaining", this.remaining);
		map.put("reset", this.reset);
		map.put("used", this.used);
		return map;
	}

}
