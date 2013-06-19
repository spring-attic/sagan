package org.springframework.site.guides;

import org.junit.Test;
import org.springframework.site.guides.Guide;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class GuideTests {

	@Test
	public void guideId(){
		Guide guide = new Guide();
		guide.setName("gs-rest-service");
		assertThat(guide.getGuideId(), is("rest-service"));
	}

	@Test
	public void isNotAGettingStartedGuide(){
		Guide guide = new Guide();
		guide.setName("not-a-guide");
		assertFalse(guide.isGettingStartedGuide());
	}

	@Test
	public void isAGettingStartedGuide(){
		Guide guide = new Guide();
		guide.setName("gs-rest-service");
		assertTrue(guide.isGettingStartedGuide());
	}
}
