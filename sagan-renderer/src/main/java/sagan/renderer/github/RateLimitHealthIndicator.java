package sagan.renderer.github;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class RateLimitHealthIndicator extends AbstractHealthIndicator {

	private final GithubClient githubClient;

	public RateLimitHealthIndicator(GithubClient githubClient) {
		this.githubClient = githubClient;
	}

	@Override
	protected void doHealthCheck(Health.Builder builder) throws Exception {
		RateLimit rateLimitInfo = this.githubClient.fetchRateLimitInfo();
		builder = builder.withDetails(rateLimitInfo.asMap());
		if (rateLimitInfo.getRemaining() > 0) {
			builder.up();
		}
		else {
			builder.outOfService();
		}
	}
}
