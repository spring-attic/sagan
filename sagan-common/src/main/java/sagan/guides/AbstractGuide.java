package sagan.guides;

import java.util.Set;

abstract class AbstractGuide extends AbstractDocument implements Guide {

    private final GuideMetadata metadata;
    private final ImageProvider imageProvider;

    @SuppressWarnings("rawtypes")
    public AbstractGuide(GuideMetadata metadata, ContentProvider contentProvider, ImageProvider imageProvider) {
        super(contentProvider);
        this.metadata = metadata;
        this.imageProvider = imageProvider;
    }

    @Override
    public byte[] getImage(String name) {
        return imageProvider.loadImage(this, name);
    }

    // --- GuideMetadata delegate methods ---

    @Override
    public String getTitle() {
        return metadata.getTitle();
    }

    @Override
    public String getSubtitle() {
        return metadata.getSubtitle();
    }

    @Override
    public Set<String> getTags() {
        return metadata.getTags();
    }

    @Override
    public String getRepoName() {
        return metadata.getRepoName();
    }

    @Override
    public String getGuideId() {
        return metadata.getGuideId();
    }

    @Override
    public String getGitRepoHttpsUrl() {
        return metadata.getGitRepoHttpsUrl();
    }

    @Override
    public String getGithubHttpsUrl() {
        return metadata.getGithubHttpsUrl();
    }

    @Override
    public String getZipUrl() {
        return metadata.getZipUrl();
    }

    @Override
    public String getGitRepoSshUrl() {
        return metadata.getGitRepoSshUrl();
    }

    @Override
    public String getGitRepoSubversionUrl() {
        return metadata.getGitRepoSubversionUrl();
    }

    @Override
    public String getCiStatusImageUrl() {
        return metadata.getCiStatusImageUrl();
    }

    @Override
    public String getCiLatestUrl() {
        return metadata.getCiLatestUrl();
    }

}
