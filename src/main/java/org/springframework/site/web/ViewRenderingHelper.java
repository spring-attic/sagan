package org.springframework.site.web;

/**
 * Convenient helper for view rendering.
 * @author Dave Syer
 * 
 */
public class ViewRenderingHelper {

	public String navClass(String active, String current) {
		if (active.equals(current)) {
			return "navbar-link active";
		} else {
			return "navbar-link";
		}
	}

	public String blogClass(String active, String current) {
		if (active.equals(current)) {
			return "blog-category active";
		} else {
			return "blog-category";
		}
	}

}
