package sagan.team;

public class MemberProfileBuilder {

    private Long id;
    private String name;
    private String username;
    private String location;
    private String bio;
    private String avatarUrl;
    private GeoLocation geoLocation;
    private boolean hidden;

    public MemberProfileBuilder() {
        this.name = "Mr England";
        this.username = "mr_england";
        this.location = "London";
        this.bio = "I am interesting";
        this.avatarUrl = null;
        this.geoLocation = null;
        this.hidden = false;
    }

    public static MemberProfileBuilder profile() {
        return new MemberProfileBuilder();
    }

    public MemberProfileBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public MemberProfileBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MemberProfileBuilder username(String username) {
        this.username = username;
        return this;
    }

    public MemberProfileBuilder location(String location) {
        this.location = location;
        return this;
    }

    public MemberProfileBuilder bio(String bio) {
        this.bio = bio;
        return this;
    }

    public MemberProfileBuilder avatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public MemberProfileBuilder geoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
        return this;
    }

    public MemberProfileBuilder geoLocation(float latitude, float longitude) {
        this.geoLocation = new GeoLocation(latitude, longitude);
        return this;
    }

    public MemberProfileBuilder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public MemberProfile build() {
        MemberProfile profile = new MemberProfile(this.id);
        profile.setName(name);
        profile.setUsername(username);
        profile.setLocation(location);
        profile.setBio(bio);
        profile.setAvatarUrl(avatarUrl);
        profile.setGeoLocation(geoLocation);
        profile.setHidden(hidden);
        return profile;
    }
}
