package org.springframework.test.guides;

import org.junit.Test;
import org.springframework.site.guides.Guide;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;

public class GuideTest {

	@Test
	public void guideId(){
		Guide guide = new Guide();
		guide.setName("gs-rest-service");
		assertEquals("rest-service", guide.getGuideId());
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
