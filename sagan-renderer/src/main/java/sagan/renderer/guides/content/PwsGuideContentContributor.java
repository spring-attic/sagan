package sagan.renderer.guides.content;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import sagan.renderer.guides.GuideContentResource;
import sagan.renderer.guides.GuideRenderingException;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

/**
 * Looks for a "push-to-pws/button.yml" and contribute its content to the guide
 */
@Component
public class PwsGuideContentContributor implements GuideContentContributor {

	private static final String PWS_METADATA_FILENAME = "push-to-pws" + File.separator + "button.yml";

	@Override
	public void contribute(GuideContentResource guideContent, File repositoryRoot) {
		try {
			File pushToPwsMetadataFile = new File(
					repositoryRoot.getAbsolutePath() + File.separator + PWS_METADATA_FILENAME);
			if (pushToPwsMetadataFile.exists()) {
				guideContent.setPushToPwsMetadata(FileCopyUtils.copyToString(new FileReader(pushToPwsMetadataFile)));
			}
		}
		catch (IOException e) {
			throw new GuideRenderingException(guideContent.getName(), e);
		}
	}
	
}
