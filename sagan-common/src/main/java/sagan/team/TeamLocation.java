package sagan.team;

public class TeamLocation {
    private String name;
    private float latitude;
    private float longitude;
    private Long memberId;

    public TeamLocation() {
    }

    public TeamLocation(String name, float latitude, float longitude, Long memberId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getMemberId() {
        return memberId;
    }
}
