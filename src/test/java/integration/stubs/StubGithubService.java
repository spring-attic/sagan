package integration.stubs;

import org.springframework.context.annotation.Primary;
import org.springframework.site.domain.guides.GuideRepo;
import org.springframework.site.domain.services.GitHubService;
import org.springframework.social.github.api.GitHubRepo;
import org.springframework.stereotype.Service;

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
	public String getRawFileAsHtml(String path) {
		return "HTML";
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
	public byte[] getImage(String imagesPath, String repoName, String imageName) {
		return new byte[]{};
	}
}
