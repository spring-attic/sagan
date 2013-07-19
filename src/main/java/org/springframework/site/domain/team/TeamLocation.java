package org.springframework.site.domain.team;

public class TeamLocation {
	private String name;
	private float latitude;
	private float longitude;
	private String memberId;

	public TeamLocation() {
	}

	public TeamLocation(String name, float latitude, float longitude, String memberId) {
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

	public String getMemberId() {
		return memberId;
	}
}
