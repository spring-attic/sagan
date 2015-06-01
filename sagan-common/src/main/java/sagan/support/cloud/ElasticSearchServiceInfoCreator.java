package sagan.support.cloud;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.Map;

public class ElasticSearchServiceInfoCreator extends CloudFoundryServiceInfoCreator<ElasticSearchServiceInfo> {

    public ElasticSearchServiceInfoCreator() {
        super(new Tags("elasticsearch", "search"), ElasticSearchServiceInfo.HTTP_SCHEME);
    }

    @Override
    public ElasticSearchServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        String id = (String) serviceData.get("name");
        Map<String, Object> credentials = (Map<String, Object>) serviceData.get("credentials");

        String uri = getStringFromCredentials(credentials, "uri");
        String sslUri = getStringFromCredentials(credentials, "sslUri");

        return new ElasticSearchServiceInfo(id, uri, sslUri);
    }
}
