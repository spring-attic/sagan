package org.springframework.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.site.HomeController;

import static org.junit.Assert.assertEquals;

public class HomeControllerTests {

	private HomeController controller;

	@Before
	public void setup() {
		controller = new HomeController();
	}

	@Test
	public void testHome() {
		assertEquals("index", controller.home());
	}

}
