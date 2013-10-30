package sagan.guides;

public interface Guide extends Document, GuideMetadata {

    byte[] getImage(String name);

}
