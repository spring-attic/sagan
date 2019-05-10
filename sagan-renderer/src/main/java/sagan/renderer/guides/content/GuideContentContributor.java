package sagan.renderer.guides.content;

import java.io.File;

import sagan.renderer.guides.GuideContentResource;

/**
 * Contribute information to the Guide content.
 */
public interface GuideContentContributor {

	/**
	 * Contribute to the guide content by extracting information from the guide repository.
	 * @param guideContent the guide content to contribute to
	 * @param repositoryRoot the unzipped repository root folder
	 */
	void contribute(GuideContentResource guideContent, File repositoryRoot);

}
