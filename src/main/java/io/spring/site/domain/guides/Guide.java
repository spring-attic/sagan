package io.spring.site.domain.guides;

public class Guide extends GuideWithoutContent{
    private final String content;
    private final String sidebar;

    public Guide(String repoName, String guideId, String title, String subtitle, String content, String sidebar) {
        super(repoName, guideId, title, subtitle);
        this.content = content;
        this.sidebar = sidebar;
    }

    public String getContent() {
        return content;
    }

    public String getSidebar() {
        return sidebar;
    }

}
