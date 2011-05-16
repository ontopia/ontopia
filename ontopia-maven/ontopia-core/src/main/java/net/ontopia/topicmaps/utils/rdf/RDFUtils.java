
package net.ontopia.topicmaps.utils.rdf;

import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import net.ontopia.utils.OntopiaRuntimeException;

import com.hp.hpl.jena.rdf.arp.*;

/**
 * INTERNAL: Various utilities for working with RDF.
 */
public class RDFUtils {

  /**
   * Parses the RDF/XML at the given URL into the given StatementHandler.
   */
  public static void parseRDFXML(String url, StatementHandler handler)
    throws IOException {
    ARP parser = new ARP();
    parser.getHandlers().setStatementHandler(handler);

    URLConnection conn = new URL(url).openConnection();
    String encoding = conn.getContentEncoding();
    InputStream in = null;
    try {
      in = conn.getInputStream();
      if (encoding == null)
        parser.load(in, url);
      else
        parser.load(new InputStreamReader(in, encoding), url);
      in.close();
    } catch (org.xml.sax.SAXException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (in != null)
        in.close();
    }
  }
}