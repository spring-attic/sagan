package sagan.support;

import org.tuckey.web.filters.urlrewrite.Conf;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.URL;

/**
 * Subclass of {@link org.tuckey.web.filters.urlrewrite.UrlRewriteFilter}
 * that overrides the configuration file loading mechanism.
 * 
 * @author Brian Clozel
 */
public class TuckeyRewriteFilter extends org.tuckey.web.filters.urlrewrite.UrlRewriteFilter {

    @Override
    protected void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
        String confPath = filterConfig.getInitParameter("confPath");
        ServletContext context = filterConfig.getServletContext();
        try {
            final URL confUrl = getClass().getClassLoader().getResource(confPath);
            final InputStream config = getClass().getClassLoader().getResourceAsStream(confPath);
            Conf conf = new Conf(context, config, confPath, confUrl.toString(), false);
            checkConf(conf);
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }

}
