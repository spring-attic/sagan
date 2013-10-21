package sagan.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sagan.util.service.DateService;
import sagan.team.MemberProfile;
import sagan.team.service.TeamRepository;
import sagan.blog.Post;
import sagan.blog.PostForm;

import java.util.Date;

@Service
public class PostFormAdapter {
    private static final int SUMMARY_LENGTH = 500;

    private final BlogPostContentRenderer renderer;
    private final SummaryExtractor summaryExtractor;
    private final DateService dateService;
    private final TeamRepository teamRepository;

    @Autowired
    public PostFormAdapter(BlogPostContentRenderer renderer, SummaryExtractor summaryExtractor, DateService dateService, TeamRepository teamRepository) {
        this.renderer = renderer;
        this.summaryExtractor = summaryExtractor;
        this.dateService = dateService;
        this.teamRepository = teamRepository;
    }

    public Post createPostFromPostForm(PostForm postForm, String username) {
        String content = postForm.getContent();
        Post post = new Post(postForm.getTitle(), content, postForm.getCategory());
        MemberProfile profile = this.teamRepository.findByUsername(username);
        post.setAuthor(profile);
        post.setCreatedAt(createdDate(postForm, dateService.now()));

        setPostProperties(postForm, content, post);
        return post;
    }

    public void updatePostFromPostForm(Post post, PostForm postForm) {
        String content = postForm.getContent();

        post.setTitle(postForm.getTitle());
        post.setRawContent(content);
        post.setCategory(postForm.getCategory());
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
        post.setRenderedContent(renderer.render(content));
        summarize(post);
        post.setBroadcast(postForm.isBroadcast());
        post.setDraft(postForm.isDraft());
        post.setPublishAt(publishDate(postForm));
    }

    private Date publishDate(PostForm postForm) {
        if (!postForm.isDraft() && postForm.getPublishAt() == null) {
            return this.dateService.now();
        } else {
            return postForm.getPublishAt();
        }
    }

    public void summarize(Post post) {
        String summary = summaryExtractor.extract(post.getRenderedContent(), SUMMARY_LENGTH);
        post.setRenderedSummary(summary);
    }
}