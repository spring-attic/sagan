package sagan;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.Profiles;

import static java.lang.String.format;
import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;
import static sagan.SaganProfiles.*;

/**
 * A specialized {@link SpringApplication} that enforces {@link SaganProfiles} semantics.
 * See sagan.SiteApplication and sagan.IndexerApplication classes for usage examples.
 */
public class SaganApplication extends SpringApplication {

    private static final Log logger = LogFactory.getLog(SaganApplication.class);

    public SaganApplication(Class<?> configClass) {
        super(configClass);
    }

    public static SpringApplication create(Class<?> configClass) {
        try {
            return new SaganApplication(configClass);
        } catch (Throwable e) {
            try {
                return BeanUtils.instantiateClass(SpringApplication.class.getConstructor(Object[].class), new Object[] {
                        new Object[] {
                                configClass.getName() } });
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * Enforce mutual exclusivity and implicit activation of profiles as described in
     * {@link SaganProfiles}.
     */
    @Override
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
        super.configureProfiles(environment, args);

        boolean stagingActive = environment.acceptsProfiles(Profiles.of(STAGING));
        boolean productionActive = environment.acceptsProfiles(Profiles.of(PRODUCTION));

        if (stagingActive && productionActive) {
            throw new IllegalStateException(format("Only one of the following profiles may be specified: [%s]",
                    arrayToCommaDelimitedString(new String[] { STAGING, PRODUCTION })));
        }

        if (stagingActive || productionActive) {
            logger.info(format("Activating '%s' profile because one of '%s' or '%s' profiles have been specified.",
                    CLOUDFOUNDRY, STAGING, PRODUCTION));
            environment.addActiveProfile(CLOUDFOUNDRY);
        } else {
            logger.info("The default 'standalone' profile is active because no other profiles have been specified.");
            environment.addActiveProfile(STANDALONE);
            Map<String, Object> map = new HashMap<>();
            map.put("client.dir", new File("../sagan-client").getAbsolutePath());
            environment.getPropertySources().addLast(new MapPropertySource("clientDir", map));
        }

    }
}
