package sagan;

import sagan.support.TuckeyRewriteFilter;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UrlRewriterFilterConfig {

	public static final String REWRITE_FILTER_NAME = "rewriteFilter";
	public static final String REWRITE_FILTER_CONF_PATH = "urlrewrite.xml";

	
	@Bean
	public FilterRegistrationBean<?> rewriteFilterConfig() {
		FilterRegistrationBean<Filter> reg = new FilterRegistrationBean<>();
		reg.setName(REWRITE_FILTER_NAME);
		reg.setFilter(new TuckeyRewriteFilter());
		reg.addInitParameter("confPath", REWRITE_FILTER_CONF_PATH);
		reg.addInitParameter("confReloadCheckInterval", "-1");
		reg.addInitParameter("statusPath", "/redirect");
		reg.addInitParameter("statusEnabledOnHosts", "*");
		reg.addInitParameter("logLevel", "WARN");
		return reg;
	}
}
