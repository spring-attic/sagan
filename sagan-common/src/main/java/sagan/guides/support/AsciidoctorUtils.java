package sagan.guides.support;

import sagan.support.ResourceNotFoundException;
import sagan.support.github.Readme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.client.RestClientException;

/**
 * Various utilities to help with asciidoctor processing
 *
 * @author Greg Turnquist
 */
public class AsciidoctorUtils {

    private static final Log log = LogFactory.getLog(AsciidoctorUtils.class);

    public Readme getReadme(GuideOrganization org, String path) {
        try {
            log.debug(String.format("Fetching README for '%s'", path));
            return org.getReadme(path);
        } catch (RestClientException ex) {
            String msg = String.format("No README found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

    public AsciidocGuide getDocument(GuideOrganization org, String path) {
        try {
            log.debug(String.format("Fetching content for '%s'", path));
            return org.getAsciidocGuide(path);
        } catch (RestClientException ex) {
            String msg = String.format("No content found for '%s'", path);
            log.warn(msg, ex);
            throw new ResourceNotFoundException(msg, ex);
        }
    }

}
