package sagan.blog.support;

import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import saganx.AbstractIntegrationTests;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Integration tests ensuring that caching functionality works as expected in
 * {@link BlogService}.
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
        ReflectionUtils.doWithMethods(BlogService.class, (method) -> {

            String methodName = method.getName();
            Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
            CacheEvict cacheEvict = AnnotationUtils.findAnnotation(method, CacheEvict.class);

            if (methodName.equals("getPublishedPost")) {
                assertNotNull("Method " + methodName + " was expected to have Cacheable annotation.", cacheable);
                String[] cacheName = cacheable.value();
                assertThat(cacheName[0], equalTo(BlogService.CACHE_NAME));
            }
            else if (methodName.equals("deletePost") || methodName.equals("updatePost")) {
                assertNotNull("Method " + methodName + " was expected to have CacheEvict annotation.", cacheEvict);
                String[] cacheName = cacheEvict.value();
                assertThat(cacheName[0], equalTo(BlogService.CACHE_NAME));
            }
            else {
                assertNull("Method " + methodName + " was not expected to have Cacheable annotation.", cacheable);
                assertNull("Method " + methodName + " was not expected to have CacheEvict annotation.", cacheEvict);
            }
        });
    }
}
