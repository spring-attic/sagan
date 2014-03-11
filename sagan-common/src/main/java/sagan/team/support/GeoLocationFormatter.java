package sagan.team.support;

import sagan.team.GeoLocation;

import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
class GeoLocationFormatter implements Formatter<GeoLocation> {

    public static final Pattern PATTERN = Pattern.compile("(-?\\d+(?:\\.\\d+)?)\\s*,\\s*(-?\\d+(?:\\.\\d+)?)");

    @Override
    public GeoLocation parse(String text, Locale locale) throws ParseException {
        Matcher m = PATTERN.matcher(text);
        if (!m.find()) {
            throw new ParseException(text, 0);
        }
        float latitude = Float.valueOf(m.group(1));
        float longitude = Float.valueOf(m.group(2));
        return new GeoLocation(latitude, longitude);
    }

    @Override
    public String print(GeoLocation location, Locale locale) {
        return String.format(Locale.US, "%f,%f", location.getLatitude(), location.getLongitude());
    }
}
