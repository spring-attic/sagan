package sagan.team.support;

import sagan.team.GeoLocation;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class GeoLocationFormatterTests {
    private GeoLocationFormatter formatter;

    @Before
    public void setup() {
        formatter = new GeoLocationFormatter();
    }

    @Test
    public void testParse() throws Exception {
        assertLatLon("1,1", 1f, 1f);
        assertLatLon("1.1,1.1", 1.1f, 1.1f);
        assertLatLon("-90.0,-180", -90f, -180f);
        assertLatLon("1.1 , 1.1", 1.1f, 1.1f);
    }

    @Test(expected = ParseException.class)
    public void testNoParse() throws Exception {
        formatter.parse("afslk", null);
    }

    private void assertLatLon(String latLon, float lat, float lon) throws ParseException {
        GeoLocation location = formatter.parse(latLon, null);
        assertThat(location.getLatitude(), equalTo(lat));
        assertThat(location.getLongitude(), equalTo(lon));
    }

    @Test
    public void testPrint() throws Exception {
        assertThat(formatter.print(new GeoLocation(-10.3f, 87.42f), null), equalTo("-10.300000,87.419998"));
    }
}
