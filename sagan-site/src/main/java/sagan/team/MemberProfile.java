package sagan.team;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import org.springframework.util.StringUtils;

@Entity
public class MemberProfile {

    public static final MemberProfile NOT_FOUND = new MemberProfile();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String jobTitle;

    @Column(nullable = true)
    private String location;

    @Column(nullable = true)
    @Type(type = "text")
    private String bio;

    @Column(nullable = true)
    private String avatarUrl;

    @Column(nullable = true)
    private String gravatarEmail;

    @Column(nullable = true)
    private String githubUsername;

    @Column(nullable = false)
    private String username;

    @Column(nullable = true)
    private String speakerdeckUsername;

    @Column(nullable = true)
    private String twitterUsername;

    @Column(nullable = true)
    private String lanyrdUsername;

    @Column(nullable = true)
    private Long githubId;

    @Column
    private GeoLocation geoLocation;

    @Column
    @Type(type = "text")
    private String videoEmbeds;

    @Column
    private boolean hidden;

    public MemberProfile() {
    }

    /** For unit testing purposes */
    MemberProfile(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String getFullName() {
        return name == null ? getUsername() : name;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    public void setGithubUsername(String githubUsername) {
        this.githubUsername = githubUsername;
    }

    public boolean hasGithubUsername() {
        return !StringUtils.isEmpty(githubUsername);
    }

    public Long getGithubId() {
        return githubId;
    }

    public void setGithubId(Long githubId) {
        this.githubId = githubId;
    }

    public String getGravatarEmail() {
        return gravatarEmail;
    }

    public void setGravatarEmail(String gravatarEmail) {
        this.gravatarEmail = gravatarEmail;
    }

    public String getSpeakerdeckUsername() {
        return speakerdeckUsername;
    }

    public void setSpeakerdeckUsername(String speakerdeckUsername) {
        this.speakerdeckUsername = speakerdeckUsername;
    }

    public boolean hasSpeakerdeckUsername() {
        return !StringUtils.isEmpty(speakerdeckUsername);
    }

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public boolean hasTwitterUsername() {
        return !StringUtils.isEmpty(twitterUsername);
    }

    public String getLanyrdUsername() {
        return lanyrdUsername;
    }

    public void setLanyrdUsername(String lanyrdUsername) {
        this.lanyrdUsername = lanyrdUsername;
    }

    public boolean hasLanyrdUsername() {
        return !StringUtils.isEmpty(lanyrdUsername);
    }

    @JsonIgnore
    public Link getTwitterLink() {
        if (StringUtils.isEmpty(getTwitterUsername())) {
            return null;
        }
        return new Link(String.format("http://twitter.com/%s", getTwitterUsername()), "@" + getTwitterUsername());
    }

    @JsonIgnore
    public Link getSpeakerdeckLink() {
        if (StringUtils.isEmpty(getSpeakerdeckUsername())) {
            return null;
        }
        String pathAndHost = String.format("speakerdeck.com/%s", getSpeakerdeckUsername());
        return new Link("https://" + pathAndHost, pathAndHost);
    }

    @JsonIgnore
    public Link getGithubLink() {
        if (StringUtils.isEmpty(getGithubUsername())) {
            return null;
        }
        String pathAndHost = String.format("github.com/%s", getGithubUsername());
        return new Link("https://" + pathAndHost, pathAndHost);
    }

    @JsonIgnore
    public Link getLanyrdLink() {
        if (StringUtils.isEmpty(getLanyrdUsername())) {
            return null;
        }
        String pathAndHost = String.format("lanyrd.com/profile/%s", getLanyrdUsername());
        return new Link("https://" + pathAndHost, pathAndHost);
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    @JsonIgnore
    public TeamLocation getTeamLocation() {
        if (geoLocation == null)
            return null;
        return new TeamLocation(name, geoLocation.getLatitude(), geoLocation.getLongitude(), getId());
    }

    public String getVideoEmbeds() {
        return videoEmbeds;
    }

    public void setVideoEmbeds(String videoEmbeds) {
        this.videoEmbeds = videoEmbeds;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MemberProfile that = (MemberProfile) o;

        if (hidden != that.hidden)
            return false;
        if (avatarUrl != null ? !avatarUrl.equals(that.avatarUrl) : that.avatarUrl != null)
            return false;
        if (bio != null ? !bio.equals(that.bio) : that.bio != null)
            return false;
        if (geoLocation != null ? !geoLocation.equals(that.geoLocation) : that.geoLocation != null)
            return false;
        if (githubId != null ? !githubId.equals(that.githubId) : that.githubId != null)
            return false;
        if (githubUsername != null ? !githubUsername.equals(that.githubUsername) : that.githubUsername != null)
            return false;
        if (gravatarEmail != null ? !gravatarEmail.equals(that.gravatarEmail) : that.gravatarEmail != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (jobTitle != null ? !jobTitle.equals(that.jobTitle) : that.jobTitle != null)
            return false;
        if (lanyrdUsername != null ? !lanyrdUsername.equals(that.lanyrdUsername) : that.lanyrdUsername != null)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (speakerdeckUsername != null ? !speakerdeckUsername.equals(that.speakerdeckUsername)
                : that.speakerdeckUsername != null)
            return false;
        if (twitterUsername != null ? !twitterUsername.equals(that.twitterUsername) : that.twitterUsername != null)
            return false;
        if (!username.equals(that.username))
            return false;
        if (videoEmbeds != null ? !videoEmbeds.equals(that.videoEmbeds) : that.videoEmbeds != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (jobTitle != null ? jobTitle.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (bio != null ? bio.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (gravatarEmail != null ? gravatarEmail.hashCode() : 0);
        result = 31 * result + (githubUsername != null ? githubUsername.hashCode() : 0);
        result = 31 * result + username.hashCode();
        result = 31 * result + (speakerdeckUsername != null ? speakerdeckUsername.hashCode() : 0);
        result = 31 * result + (twitterUsername != null ? twitterUsername.hashCode() : 0);
        result = 31 * result + (lanyrdUsername != null ? lanyrdUsername.hashCode() : 0);
        result = 31 * result + (githubId != null ? githubId.hashCode() : 0);
        result = 31 * result + (geoLocation != null ? geoLocation.hashCode() : 0);
        result = 31 * result + (videoEmbeds != null ? videoEmbeds.hashCode() : 0);
        result = 31 * result + (hidden ? 1 : 0);
        return result;
    }
}
