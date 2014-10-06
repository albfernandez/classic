package cz.muni.fi.xharting.classic.bootstrap.scan;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractScanner implements Scanner {

    private static final Logger log = LoggerFactory.getLogger(AbstractScanner.class);

    private static final String[] SEAM_ARCHIVE_MARKERS = { "seam.properties", "META-INF/seam.properties", "META-INF/components.xml" };
    private static final String[] SEAM_ARCHIVE_BLACKLIST = { "org/jboss/integration/ext-content/main/bundled/jboss-seam-int.jar" };

    protected String[] getSeamArchiveMarkers() {
        return SEAM_ARCHIVE_MARKERS;
    }

    protected Collection<URL> getSeamArchives(ClassLoader loader) {
        Set<URL> archives = new HashSet<URL>();
        for (String resourceName : getSeamArchiveMarkers()) {
            Enumeration<URL> urls;
            try {
                urls = loader.getResources(resourceName);
            } catch (IOException e) {
                log.warn("Unable to load Seam resource {}", resourceName, e);
                continue;
            }
            while (urls.hasMoreElements()) {
                URL resource = urls.nextElement();
                String resourcePath = resource.getFile();
                try {
                    resourcePath = URLDecoder.decode(resourcePath, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.warn("Unable to decode URL {}", resourcePath);
                }
                log.debug("Found Seam resource at the following url {} and path {}", resource, resourcePath);

                if (resourcePath.indexOf('!') > 0) { // no idea what ! in the path means
                    resourcePath = resourcePath.substring(0, resourcePath.indexOf('!'));
                } else {
                    File dirOrArchive = new File(resourcePath);
                    if (resourceName != null && resourceName.lastIndexOf('/') > 0) {
                        // for META-INF/components.xml
                        dirOrArchive = dirOrArchive.getParentFile();
                    }
                    resourcePath = dirOrArchive.getParent();
                }
                URL archiveUrl;
                try {
                    archiveUrl = new URL(resource.getProtocol(), resource.getHost(), resource.getPort(), resourcePath);
                } catch (MalformedURLException e) {
                    throw new IllegalStateException("Unable to identify Seam archive " + resource, e);
                }
                log.debug("Identified Seam archive at {}", archiveUrl);
                archives.add(archiveUrl);
            }
        }
        return filterSeamArchives(archives);
    }

    protected Collection<URL> filterSeamArchives(Collection<URL> archives) {
        for (Iterator<URL> iterator = archives.iterator(); iterator.hasNext();) {
            URL url = iterator.next();
            for (String blacklistEntry : SEAM_ARCHIVE_BLACKLIST) {
                if (url.toString().matches(".*" + blacklistEntry + ".*")) {
                    iterator.remove();
                    break;
                }
            }
        }
        return archives;
    }
}
