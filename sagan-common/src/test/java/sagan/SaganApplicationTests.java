package sagan;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import static org.springframework.util.StringUtils.arrayToCommaDelimitedString;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;
import static sagan.SaganProfiles.*;

public class SaganApplicationTests {

    @BeforeClass
    public static void assertClear() {
        assertThat(System.getProperty(ACTIVE_PROFILES_PROPERTY_NAME), nullValue());
    }

    @Test
    public void unknownProfileSpecified() {
        activateProfiles("bogus");
        runApp();
        assertThat(runApp().getEnvironment().acceptsProfiles(STANDALONE), is(true));
        assertThat(runApp().getEnvironment().acceptsProfiles("bogus"), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void bothStagingAndProductionSpecified() {
        activateProfiles(STAGING, PRODUCTION);
        runApp();
    }

    @Test
    public void stagingSpecified() {
        activateProfiles(STAGING);
        assertThat(runApp().getEnvironment().acceptsProfiles(CLOUDFOUNDRY), is(true));
    }

    @Test
    public void productionSpecified() {
        activateProfiles(PRODUCTION);
        assertThat(runApp().getEnvironment().acceptsProfiles(CLOUDFOUNDRY), is(true));
    }

    @Test
    public void noProfileSpecified() {
        // activateProfiles(...);
        Environment env = runApp().getEnvironment();
        assertThat(env.acceptsProfiles(env.getDefaultProfiles()), is(false));
        assertThat(env.acceptsProfiles(CLOUDFOUNDRY), is(false));
        assertThat(env.acceptsProfiles(STANDALONE), is(true));
    }

    @After
    public void clearProperty() {
        System.clearProperty(ACTIVE_PROFILES_PROPERTY_NAME);
    }

    private ConfigurableApplicationContext runApp() {
        return new SaganApplication(Dummy.class).run();
    }

    private void activateProfiles(String... profiles) {
        System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, arrayToCommaDelimitedString(profiles));
    }

}

class Dummy {
}
