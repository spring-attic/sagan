package sagan.team;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class GeoLocation {

    @Column(name = "latitude")
    private float latitude;

    @Column(name = "longitude")
    private float longitude;

    public GeoLocation() {
    }

    public GeoLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
