package sagan.guides;

public interface ImageProvider {

    byte[] loadImage(GuideMetadata guide, String imageName);

}
