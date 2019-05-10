package sagan.site.guides;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface Guide extends GuideHeader {

	String getContent();

	String getTableOfContents();

	Optional<byte[]> getImageContent(String imageName);

	String getPushToPwsUrl();
	
}
