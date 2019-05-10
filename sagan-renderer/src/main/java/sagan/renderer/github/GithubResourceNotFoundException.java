package sagan.renderer.github;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GithubResourceNotFoundException extends RuntimeException {

	private final String resourceName;

	public GithubResourceNotFoundException(String orgName, String repositoryName, Throwable cause) {
		super("Could not find github repository [" + orgName + "/" + repositoryName + "]", cause);
		this.resourceName= "Repository [" + orgName + "/" + repositoryName + "]";
	}

	public GithubResourceNotFoundException(String orgName, Throwable cause) {
		super("Could not fing github organization [" + orgName + "]", cause);
		this.resourceName = "Organization [" + orgName + "]";
	}

	public String getResourceName() {
		return this.resourceName;
	}
}
