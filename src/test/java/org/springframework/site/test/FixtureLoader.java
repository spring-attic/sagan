package org.springframework.site.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FixtureLoader {

	public static String load(String path) {
		try {
			InputStream stream = new ClassPathResource(path, FixtureLoader.class).getInputStream();
			return StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
