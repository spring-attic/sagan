package sagan.renderer.github;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RateLimitHealthIndicatorTests {

	@Mock
	private GithubClient client;

	private RateLimitHealthIndicator healthIndicator;

	@BeforeEach
	public void setup() {
		this.healthIndicator = new RateLimitHealthIndicator(this.client);
	}

	@Test
	void rateLimitUp() {
		given(this.client.fetchRateLimitInfo()).willReturn(createRateLimit(10));
		Health result = this.healthIndicator.getHealth(true);
		assertThat(result.getStatus()).isEqualTo(Status.UP);
	}

	@Test
	void rateLimitOutOfService() {
		given(this.client.fetchRateLimitInfo()).willReturn(createRateLimit(0));
		Health result = this.healthIndicator.getHealth(true);
		assertThat(result.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
	}

	RateLimit createRateLimit(Integer remaining) {
		Map<String, String> map = new HashMap<>();
		map.put("limit", "60");
		map.put("used", "10");
		map.put("remaining", remaining.toString());
		map.put("reset", Long.toString(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()));
		return new RateLimit(map);
	}
}
