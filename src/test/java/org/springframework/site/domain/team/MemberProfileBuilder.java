package org.springframework.site.domain.team;

import org.springframework.test.util.ReflectionTestUtils;

public class MemberProfileBuilder {

	private Long id;
	private String name;
	private String username;
	private String location;
	private String bio;
	private String avatarUrl;
	private GeoLocation geoLocation;

	public MemberProfileBuilder() {
		this.name = "Mr England";
		this.username = "mr_england";
		this.location = "London";
		this.bio = "I am interesting";
		this.avatarUrl = null;
		this.geoLocation = null;
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

	public MemberProfile build() {
		MemberProfile profile = new MemberProfile();
		profile.setName(name);
		profile.setUsername(username);
		profile.setLocation(location);
		profile.setBio(bio);
		profile.setAvatarUrl(avatarUrl);
		profile.setGeoLocation(geoLocation);
		ReflectionTestUtils.setField(profile, "id", this.id);
		return profile;
	}
}
