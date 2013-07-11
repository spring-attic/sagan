package integration.stubs;

import org.springframework.context.annotation.Primary;
import org.springframework.site.domain.services.GitHubService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

@Service
@Primary
@SuppressWarnings("unused")
//Component Scanned
public class StubGithubService extends GitHubService {

	public StubGithubService() {
		super(null);
	}

	@Override
	public String renderToHtml(String markdownSource) {
		return markdownSource;
	}

	@Override
	public <T> T postForObject(String path, Object request, Class<T> responseType,
			Object... uriVariables) {
		try {
			return responseType.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T getForObject(String path, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		try {
			return responseType.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getRawFileAsHtml(String path) {
		return "HTML";
	}
}
