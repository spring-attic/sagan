package sagan;

import org.thymeleaf.spring4.view.ThymeleafViewResolver;

public class ThymeleafViewResolverCustomizer {

	public ThymeleafViewResolverCustomizer(ThymeleafViewResolver viewResolver, String applicationVersion) {

		viewResolver.addStaticVariable("saganAppVersion", applicationVersion);
	}

}
