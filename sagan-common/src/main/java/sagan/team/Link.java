package sagan.team;

public class Link {

    String href;
    String text;

    public Link(String href, String text) {
        this.href = href;
        this.text = text;
    }

    public String getHref() {
        return href;
    }

    public String getText() {
        return text;
    }
}
