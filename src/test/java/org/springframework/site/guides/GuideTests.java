package org.springframework.site.guides;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
