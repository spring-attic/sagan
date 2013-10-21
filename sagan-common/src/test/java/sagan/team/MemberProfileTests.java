package sagan.team;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MemberProfileTests {

    @Test
    public void fullNameUsesNameIfAvailable() {
        MemberProfile nick = new MemberProfile();
        nick.setUsername("nickstreet");
        nick.setName("Nick Street");
        assertThat(nick.getFullName(), equalTo("Nick Street"));
    }

    @Test
    public void fullNameFallsBackToUsername() {
        MemberProfile nick = new MemberProfile();
        nick.setUsername("nickstreet");
        nick.setName(null);
        assertThat(nick.getFullName(), equalTo("nickstreet"));
    }

    @Test
    public void twitterLink() {
        MemberProfile nick = new MemberProfile();
        nick.setTwitterUsername("nickstreet");
        assertThat(nick.getTwitterLink().getHref(), equalTo("http://twitter.com/nickstreet"));
        assertThat(nick.getTwitterLink().getText(), equalTo("@nickstreet"));
    }

    @Test
    public void nullTwitterLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getTwitterLink(), is(nullValue()));
    }

    @Test
    public void emptyTwitterLink() {
        MemberProfile nick = new MemberProfile();
        nick.setTwitterUsername("");
        assertThat(nick.getTwitterLink(), is(nullValue()));
    }

    @Test
    public void speakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        nick.setSpeakerdeckUsername("nickstreet");
        assertThat(nick.getSpeakerdeckLink().getHref(), equalTo("https://speakerdeck.com/nickstreet"));
        assertThat(nick.getSpeakerdeckLink().getText(), equalTo("speakerdeck.com/nickstreet"));
    }

    @Test
    public void emptySpeakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getSpeakerdeckLink(), is(nullValue()));
    }

    @Test
    public void nullSpeakerdeckLink() {
        MemberProfile nick = new MemberProfile();
        nick.setSpeakerdeckUsername("");
        assertThat(nick.getSpeakerdeckLink(), is(nullValue()));
    }

    @Test
    public void githubLink() {
        MemberProfile nick = new MemberProfile();
        nick.setGithubUsername("nickstreet");
        assertThat(nick.getGithubLink().getHref(), equalTo("https://github.com/nickstreet"));
        assertThat(nick.getGithubLink().getText(), equalTo("github.com/nickstreet"));
    }

    @Test
    public void emptyGithubLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getGithubLink(), is(nullValue()));
    }

    @Test
    public void nullGithubLink() {
        MemberProfile nick = new MemberProfile();
        nick.setGithubUsername("");
        assertThat(nick.getGithubLink(), is(nullValue()));
    }

    @Test
    public void lanyrdLink() {
        MemberProfile nick = new MemberProfile();
        nick.setLanyrdUsername("nickstreet");
        assertThat(nick.getLanyrdLink().getHref(), equalTo("https://lanyrd.com/profile/nickstreet"));
        assertThat(nick.getLanyrdLink().getText(), equalTo("lanyrd.com/profile/nickstreet"));
    }

    @Test
    public void emptyLanyrdLink() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.getLanyrdLink(), is(nullValue()));
    }

    @Test
    public void nullLanyrdLink() {
        MemberProfile nick = new MemberProfile();
        nick.setLanyrdUsername("");
        assertThat(nick.getLanyrdLink(), is(nullValue()));
    }

    @Test
    public void isNotHiddenByDefault() {
        MemberProfile nick = new MemberProfile();
        assertThat(nick.isHidden(), is(false));
    }
}
