package sagan.site.support.nav;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used at the class level on Controllers to indicate which
 * {@link Section} of the site-wide navigation should be highlighted.
 *
 * @see sagan.site.support.nav.Section
 * @see sagan.MvcConfig#addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Navigation {

    Section value();
}
