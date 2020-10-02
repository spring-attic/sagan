package sagan.site.team.support;

import org.junit.jupiter.api.Test;
import sagan.site.team.MemberProfile;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class MemberProfileTests {

    @Test
    public void fullNameUsesNameIfAvailable() {
        MemberProfile nick = new MemberProfile();
        nick.setUsername("nickstreet");
        nick.setName("Nick Street");
        assertThat(nick.getFullName()).isEqualTo("Nick Street");
    }

    @Test
    public void fullNameFallsBackToUsername() {
        MemberProfile nick = new MemberProfile();
        nick.setUsername("nickstreet");
        nick.setName(null);
        assertThat(nick.getFullName()).isEqualTo("nickstreet");
    }

    @Test
    public void twitterLink() {
        MemberProfile nick = new MemberProfile();
        nick.setTwitterUsername("nickstreet");
        assertThat(nick.getTwitterLink().getHref()).isEqualTo("http://twitter.com/nickstreet");
        assertThat(nick.getTwitterLink().getText()).isEqualTo("@nickstreet");
    }

    @Test
    public void nullTwitterLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getTwitterLink()).isNull();
    }

    @Test
    public void emptyTwitterLink() {
        MemberProfile nick = new MemberProfile();
        nick.setTwitterUsername("");
        assertThat(nick.getTwitterLink()).isNull();
    }

    @Test
    public void speakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        nick.setSpeakerdeckUsername("nickstreet");
        assertThat(nick.getSpeakerdeckLink().getHref()).isEqualTo("https://speakerdeck.com/nickstreet");
        assertThat(nick.getSpeakerdeckLink().getText()).isEqualTo("speakerdeck.com/nickstreet");
    }

    @Test
    public void emptySpeakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getSpeakerdeckLink()).isNull();
    }

    @Test
    public void nullSpeakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        nick.setSpeakerdeckUsername("");
        assertThat(nick.getSpeakerdeckLink()).isNull();
    }

    @Test
    public void githubLink() {
        MemberProfile nick = new MemberProfile();
        nick.setGithubUsername("nickstreet");
        assertThat(nick.getGithubLink().getHref()).isEqualTo("https://github.com/nickstreet");
        assertThat(nick.getGithubLink().getText()).isEqualTo("github.com/nickstreet");
    }

    @Test
    public void emptyGithubLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getGithubLink()).isNull();
    }

    @Test
    public void nullGithubLink() {
        MemberProfile nick = new MemberProfile();
        nick.setGithubUsername("");
        assertThat(nick.getGithubLink()).isNull();
    }

    @Test
    public void lanyrdLink() {
        MemberProfile nick = new MemberProfile();
        nick.setLanyrdUsername("nickstreet");
        assertThat(nick.getLanyrdLink().getHref()).isEqualTo("https://lanyrd.com/profile/nickstreet");
        assertThat(nick.getLanyrdLink().getText()).isEqualTo("lanyrd.com/profile/nickstreet");
    }

    @Test
    public void emptyLanyrdLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getLanyrdLink()).isNull();
    }

    @Test
    public void nullLanyrdLink() {
        MemberProfile nick = new MemberProfile();
        nick.setLanyrdUsername("");
        assertThat(nick.getLanyrdLink()).isNull();
    }

    @Test
    public void isNotHiddenByDefault() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.isHidden()).isFalse();
    }
}
