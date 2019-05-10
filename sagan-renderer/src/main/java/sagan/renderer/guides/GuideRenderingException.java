package sagan.renderer.guides;

public class GuideRenderingException extends RuntimeException {

	private final String repositoryName;

	public GuideRenderingException(String repositoryName, Throwable cause) {
		super("Could not render guide [" + repositoryName + "]", cause);
		this.repositoryName = repositoryName;
	}

	public String getRepositoryName() {
		return repositoryName;
	}
}
