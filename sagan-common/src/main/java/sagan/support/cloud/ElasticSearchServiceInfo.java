package sagan.support.cloud;

import org.springframework.cloud.service.BaseServiceInfo;
import org.springframework.cloud.util.StandardUriInfoFactory;
import org.springframework.cloud.util.UriInfo;
import org.springframework.cloud.util.UriInfoFactory;

public class ElasticSearchServiceInfo extends BaseServiceInfo {

	public static final String HTTP_SCHEME = "http";
	public static final String HTTPS_SCHEME = "https";

	private static UriInfoFactory uriFactory = new StandardUriInfoFactory();

	private UriInfo uriInfo;
	private UriInfo sslUriInfo;

	public ElasticSearchServiceInfo(String id, String uri, String sslUri) {
		super(id);
		this.uriInfo = getUriInfoFactory().createUri(uri);
		this.sslUriInfo = getUriInfoFactory().createUri(sslUri);
	}

	public UriInfoFactory getUriInfoFactory() {
		return uriFactory;
	}

	@ServiceProperty(category = "connection")
	public String getUri() {
		return uriInfo.toString();
	}

	@ServiceProperty(category = "connection")
	public String getSslUri() {
		return sslUriInfo.toString();
	}
}
