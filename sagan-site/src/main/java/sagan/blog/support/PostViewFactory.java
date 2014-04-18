package sagan.blog.support;

import sagan.blog.Post;
import sagan.support.DateFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class PostViewFactory {

    private DateFactory dateFactory;

    @Autowired
    public PostViewFactory(DateFactory dateFactory) {
        this.dateFactory = dateFactory;
    }

    public PostView createPostView(Post post) {
        return new PostView(post, dateFactory);
    }

    public Page<PostView> createPostViewPage(Page<Post> posts) {
        List<PostView> postViews = posts.getContent().stream()
                .map(this::createPostView)
                .collect(Collectors.toList());
        PageRequest pageRequest = new PageRequest(posts.getNumber(), posts.getSize(), posts.getSort());
        return new PageImpl<>(postViews, pageRequest, posts.getTotalElements());
    }
}
