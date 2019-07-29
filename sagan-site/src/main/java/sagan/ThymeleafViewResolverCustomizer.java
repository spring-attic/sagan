package sagan;

import org.thymeleaf.spring5.view.ThymeleafViewResolver;

public class ThymeleafViewResolverCustomizer {

	public ThymeleafViewResolverCustomizer(ThymeleafViewResolver viewResolver, String applicationVersion) {

		viewResolver.addStaticVariable("saganAppVersion", applicationVersion);
	}

}
