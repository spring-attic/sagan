package sagan.guides;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.team.MemberProfile;
import sagan.team.MemberProfileBuilder;

/**
 * Sagan is using Redis as a distributed cache in production.
 * To store model objects in that cache, those are (de)serialized to JSON - those tests make sure
 * that models are properly annotated to support JSON serialization.
 */
public class ModelSerializationTests {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testGuideSerialization() throws Exception {

        GettingStartedGuide guide = new GettingStartedGuide(new DefaultGuideMetadata("org", "guide", "repo", "description"));
        guide.setContent("main content");
        guide.setTableOfContents("TOC");

        String serialized = mapper.writeValueAsString(guide);
        GettingStartedGuide unserialized = mapper.readValue(serialized, GettingStartedGuide.class);

        Assert.assertNotNull(unserialized);
    }

    @Test
    public void testTutorialSerialization() throws Exception {

        Tutorial tutorial = new Tutorial(new DefaultGuideMetadata("org", "guide", "repo", "description"));
        tutorial.setContent("main content");
		tutorial.setTableOfContents("TOC");

        String serialized = mapper.writeValueAsString(tutorial);
        Tutorial unserialized = mapper.readValue(serialized, Tutorial.class);

        Assert.assertNotNull(unserialized);
    }

    @Test
    public void testUnderstandingSerialization() throws Exception {

        UnderstandingDoc doc = new UnderstandingDoc("subject", "content");
        String serialized = mapper.writeValueAsString(doc);
        UnderstandingDoc unserialized = mapper.readValue(serialized, UnderstandingDoc.class);
        Assert.assertNotNull(unserialized);
    }

    @Test
    public void testBlogPostSerialization() throws Exception {

        MemberProfile profile = MemberProfileBuilder.profile().id(1L).geoLocation(10, 5).build();
        Post post = PostBuilder.post().author(profile).build();
        String serialized = mapper.writeValueAsString(post);
        Post unserialized = mapper.readValue(serialized, Post.class);
        Assert.assertNotNull(unserialized);
    }

}
