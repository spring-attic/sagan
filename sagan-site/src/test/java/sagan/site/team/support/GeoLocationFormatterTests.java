package sagan.site.team.support;

import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sagan.site.team.GeoLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GeoLocationFormatterTests {
    private GeoLocationFormatter formatter;

    @BeforeEach
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

    @Test
    public void testNoParse() throws Exception {
        assertThatThrownBy(() -> formatter.parse("afslk", null)).isInstanceOf(ParseException.class) ;
    }

    private void assertLatLon(String latLon, float lat, float lon) throws ParseException {
        GeoLocation location = formatter.parse(latLon, null);
        assertThat(location.getLatitude()).isEqualTo(lat);
        assertThat(location.getLongitude()).isEqualTo(lon);
    }

    @Test
    public void testPrint() throws Exception {
        assertThat(formatter.print(new GeoLocation(-10.3f, 87.42f), null)).isEqualTo("-10.300000,87.419998");
    }
}
