package sagan.search.types;

import java.util.Date;

public class BlogPost extends SearchEntry {

    private Date publishAt = new Date();

    private String author;

    public Date getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Date publishAt) {
        this.publishAt = publishAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getType() {
        return SearchType.BLOG_POST.toString();
    }
}
