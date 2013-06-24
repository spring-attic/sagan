package org.springframework.site.guides;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GuideTests {

	@Test
	public void guideId(){
		GuideRepo guideRepo = new GuideRepo();
		guideRepo.setName("gs-rest-service");
		assertThat(guideRepo.getGuideId(), is("rest-service"));
	}

	@Test
	public void isNotAGettingStartedGuide(){
		GuideRepo guideRepo = new GuideRepo();
		guideRepo.setName("not-a-guide");
		assertFalse(guideRepo.isGettingStartedGuide());
	}

	@Test
	public void isAGettingStartedGuide(){
		GuideRepo guideRepo = new GuideRepo();
		guideRepo.setName("gs-rest-service");
		assertTrue(guideRepo.isGettingStartedGuide());
	}
}
