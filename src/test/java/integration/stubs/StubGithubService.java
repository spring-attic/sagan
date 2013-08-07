package integration.stubs;

import org.springframework.context.annotation.Primary;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.domain.services.github.GitHubService;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@SuppressWarnings("unused")
//Component Scanned
public class StubGithubService extends GitHubService {

	private final HashMap<String, String> rawFileMappings = new HashMap<>();
	private RuntimeException exceptionToBeThrown;

	public StubGithubService() {
		super(null, null);
	}

	@Override
	public String renderToHtml(String markdownSource) {
		return markdownSource;
	}

	@Override
	public String getRawFileAsHtml(String path) {
		if (exceptionToBeThrown != null) {
			throw exceptionToBeThrown;
		}

		for (Map.Entry<String, String> entry : rawFileMappings.entrySet()) {
			if (path.matches(entry.getKey())) {
				return entry.getValue();
			}
		}
		return "HTML";
	}

	public void addRawFileMapping(String path, String html) {
		rawFileMappings.put(path, html);
	}

	@Override
	public GitHubRepo getRepoInfo(String githubUsername, String repoName) {
		return new GitHubRepo();
	}

	@Override
	public GuideRepo[] getGuideRepos(String reposPath) {
		return new GuideRepo[]{};
	}

	@Override
	public byte[] getGuideImage(String repoName, String imageName) {
		return new byte[]{};
	}

	public void addExceptionToBeThrown(RuntimeException exception) {
		exceptionToBeThrown = exception;
	}

	public void clearStubs() {
		exceptionToBeThrown = null;
		rawFileMappings.clear();
	}
}
