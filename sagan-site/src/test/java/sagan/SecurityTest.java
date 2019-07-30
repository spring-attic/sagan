package sagan;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Rob Winch
 */
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest(controllers = SecurityTestController.class)
@ContextConfiguration(classes = SecurityTestController.class)
@TestPropertySource(properties = "spring.profiles.active=standalone")
public @interface SecurityTest {}
