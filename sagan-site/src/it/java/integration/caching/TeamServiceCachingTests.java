package integration.caching;

import sagan.team.service.TeamService;
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
 * {@link TeamService}.
 * 
 * @author Chris Beams
 */
public class TeamServiceCachingTests extends AbstractIntegrationTests {

    @Autowired
    private TeamService teamService;

    /**
     * Check that {@link #teamService} is advised for caching and that all methods
     * relating to published posts have the {@link Cacheable} annotation. Note that while
     * more extensive mocking and testing through {@code MockMvc} etc is possible, this
     * approach is less fragile and much more concise while still being likely to catch
     * configuration errors.
     */
    @Test
    public void teamServiceIsAdvisedForCaching() {
        assertThat("TeamService is not advised as expected", teamService, instanceOf(Advised.class));
        boolean hasCachingAdvisor = false;
        for (Advisor advisor : ((Advised) teamService).getAdvisors()) {
            if (advisor instanceof BeanFactoryCacheOperationSourceAdvisor) {
                hasCachingAdvisor = true;
                break;
            }
        }
        assertTrue("TeamService is advised, but does not have caching advisor", hasCachingAdvisor);
        // @formatter:off
        ReflectionUtils.doWithMethods(TeamService.class, new ReflectionUtils.MethodCallback() { // TODO: lambda
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                Cacheable cacheable = AnnotationUtils.findAnnotation(method, Cacheable.class);
                String methodName = method.getName();
                if (methodName.equals("fetchMemberProfileUsername") || methodName.equals("fetchActiveMembers")) {
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
