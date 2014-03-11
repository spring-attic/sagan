package sagan.blog.support;

import sagan.DatabaseConfig;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import integration.AbstractIntegrationTests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Integration tests ensuring that caching functionality works as expected in
 * {@link BlogService}.
 * 
 * @author Chris Beams
 */
public class BlogServiceCachingTests extends AbstractIntegrationTests {

    @Autowired
    private BlogService blogService;

    /**
     * Check that {@link #blogService} is advised for caching and that all methods
     * relating to published posts have the {@link Cacheable} annotation. Note that while
     * more extensive mocking and testing through {@code MockMvc} etc is possible, this
     * approach is less fragile and much more concise while still being likely to catch
     * configuration errors.
     */
    @Test
    public void blogServiceIsAdvisedForCaching() {
        assertThat("BlogService is not advised as expected", blogService, instanceOf(Advised.class));
        boolean hasCachingAdvisor = false;
        for (Advisor advisor : ((Advised) blogService).getAdvisors()) {
            if (advisor instanceof BeanFactoryCacheOperationSourceAdvisor) {
                hasCachingAdvisor = true;
                break;
            }
        }
        assertTrue("BlogService is advised, but does not have caching advisor", hasCachingAdvisor);
        // @formatter:off
        ReflectionUtils.doWithMethods(BlogService.class, new ReflectionUtils.MethodCallback() { // TODO: lambda
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
                String methodName = method.getName();
                if (methodName.matches("^get.*Published.*$")) {
                    assertNotNull("Method " + methodName + " was expected to have Cacheable annotation.", cacheable);
                    String[] cacheName = cacheable.value();
                    assertThat(cacheName[0], equalTo(DatabaseConfig.CACHE_NAME));
                } else {
                    assertNull("Method " + methodName + " was not expected to have Cacheable annotation.", cacheable);
                }
            }
        });
        // @formatter:on
    }

}
