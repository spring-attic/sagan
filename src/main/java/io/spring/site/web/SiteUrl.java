package io.spring.site.web;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Service
public class SiteUrl {

	public String getUrl() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();
		return requestURL.replace(requestURI, "");
	}

	public String getAbsoluteUrl(String path) {
		return getUrl() + path;
	}
}
