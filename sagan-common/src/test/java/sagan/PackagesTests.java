package sagan;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PackagesTests {

    @Test
    public void sagan() {
        assertThat(Packages.sagan, equalTo(Packages.class.getPackage().getName()));
    }
}
