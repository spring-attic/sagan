package sagan.site.webapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import sagan.ModelMapperConfig;

import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

/**
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "spring.io", uriPort = 443)
@WebMvcTest
@Import({ModelMapperConfig.class, MvcTestConfig.class})
public @interface WebApiTest {

	@AliasFor(annotation = WebMvcTest.class)
	Class<?>[] value() default {};

}
