package sagan.site.blog;

import java.util.Date;

import sagan.site.support.DateFactory;
import sagan.site.team.MemberProfile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class PostFormAdapter {
    private static final int SUMMARY_LENGTH = 500;

    private final PostContentRenderer renderer;
    private final PostSummary postSummary;
    private final DateFactory dateFactory;

    @Autowired
    public PostFormAdapter(PostContentRenderer renderer, PostSummary postSummary, DateFactory dateFactory) {
        this.renderer = renderer;
        this.postSummary = postSummary;
        this.dateFactory = dateFactory;
    }

    public Post createPostFromPostForm(PostForm postForm, MemberProfile profile) {
        String content = postForm.getContent();
        Post post = new Post(postForm.getTitle(), content, postForm.getCategory(), postForm.getFormat());
        post.setAuthor(profile);
        post.setCreatedAt(createdDate(postForm, dateFactory.now()));

        setPostProperties(postForm, content, post);
        return post;
    }

    public void updatePostFromPostForm(Post post, PostForm postForm) {
        String content = postForm.getContent();

        post.addPublicSlugAlias(post.getPublicSlug());
        post.setTitle(postForm.getTitle());
        post.setRawContent(content);
        post.setCategory(postForm.getCategory());
        post.setFormat(postForm.getFormat());
        post.setCreatedAt(createdDate(postForm, post.getCreatedAt()));

        setPostProperties(postForm, content, post);
    }

    private Date createdDate(PostForm postForm, Date defaultDate) {
        Date createdAt = postForm.getCreatedAt();
        if (createdAt == null) {
            createdAt = defaultDate;
        }
        return createdAt;
    }

    private void setPostProperties(PostForm postForm, String content, Post post) {
        String rendered = null;
        rendered = renderer.render(content, post.getFormat());
        post.setRenderedContent(rendered);
        summarize(post);
        post.setBroadcast(postForm.isBroadcast());
        post.setDraft(postForm.isDraft());
        post.setPublishAt(publishDate(postForm));
    }

    private Date publishDate(PostForm postForm) {
        if (!postForm.isDraft() && postForm.getPublishAt() == null) {
            return dateFactory.now();
        } else {
            return postForm.getPublishAt();
        }
    }

    public void summarize(Post post) {
        String summary = postSummary.forContent(post.getRenderedContent(), SUMMARY_LENGTH);
        post.setRenderedSummary(summary);
    }

    public void refreshPost(Post post) {
        post.setRenderedContent(renderer.render(post.getRawContent(), post.getFormat()));
        summarize(post);
    }
}
