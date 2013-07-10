package org.springframework.site.web.blog;

import org.springframework.format.Formatter;
import org.springframework.site.domain.blog.PostCategory;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class PostCategoryFormatter implements Formatter<PostCategory> {
	private static Map<String, PostCategory> mapping = new HashMap<String, PostCategory>();
	{
		for (PostCategory category : PostCategory.values()) {
			mapping.put(category.getUrlSlug(), category);
			mapping.put(category.name(), category);
		}
	}

	@Override
	public PostCategory parse(String text, Locale locale) throws ParseException {
		return mapping.get(text.trim());
	}

	@Override
	public String print(PostCategory category, Locale locale) {
		return category.getUrlSlug();
	}
}