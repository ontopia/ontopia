
package net.ontopia.topicmaps.utils.sdshare.client;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.ontopia.xml.DefaultXMLReaderFactory;
import net.ontopia.utils.ObjectUtils;
import net.ontopia.utils.OntopiaRuntimeException;

/**
 * INTERNAL: Represents the SDshare client configuration and can also
 * read it from a config file.
 */
public class ClientConfig {
  // this is the global default, which is used for any sources which don't
  // declare a specific check interval
  private int checkInterval; // FIXME: move to ConfigHandler??
  private Collection<SyncEndpoint> endpoints;
  private ClientBackendIF backend;
  static Logger log = LoggerFactory.getLogger(ClientConfig.class.getName());

  // --- external interface
  
  public ClientConfig() {
    this.endpoints = new ArrayList<SyncEndpoint>();
  }

  public Collection<SyncEndpoint> getEndpoints() {
    return endpoints;
  }

  public int getCheckInterval() {
    return checkInterval;
  }

  public ClientBackendIF getBackend() {
    return backend;
  }

  public static ClientConfig readConfig() {
    ClientConfig config = new ClientConfig();
    
    // locate config file as resource
    String filename = "sdshare-client.xml";
    ClassLoader cloader = ClientConfig.class.getClassLoader();
    if (cloader == null)
      throw new OntopiaRuntimeException("Cannot find class loader.");
    InputStream istream = cloader.getResourceAsStream(filename);
    if (istream == null)
      throw new OntopiaRuntimeException("Cannot find resource: " + filename);

    // actually do the parsing
    try {
      ConfigHandler handler = new ConfigHandler(config);
      XMLReader parser = new DefaultXMLReaderFactory().createXMLReader();
      parser.setContentHandler(handler);
      parser.parse(new InputSource(istream));
    } catch (Exception e) {
      throw new OntopiaRuntimeException(e);
    }

    return config;
  }

  // --- setup interface

  private void addEndpoint(SyncEndpoint endpoint) {
    endpoints.add(endpoint);
  }

  private void setCheckInterval(int checkInterval) {
    this.checkInterval = checkInterval;
  }

  private void setBackend(String classname) {
    this.backend = (ClientBackendIF) ObjectUtils.newInstance(classname);
  }
  
  // --- SAX ContentHandler

  static class ConfigHandler extends DefaultHandler {
    private ClientConfig config;   // config being loaded
    private SyncEndpoint endpoint; // current <endpoint>
    private String property;       // name of current property
    private String check;          // stored until end tag
    private String frontend;       // stored until end tag
    private StringBuilder buf;     // used to collect code
    private boolean keep;          // keep character data?

    public ConfigHandler(ClientConfig config) {
      this.config = config;
      this.buf = new StringBuilder();
    }
    
    public void startElement(String ns, String localname, String qname,
                              Attributes atts) {
      if (qname.equals("property")) {
        property = atts.getValue("name");
        keep = true;
        
      } else if (qname.equals("endpoint")) {
        String handle = atts.getValue("handle");
        endpoint = new SyncEndpoint(handle);
        config.addEndpoint(endpoint);
        
      } else if (qname.equals("source")) {
        keep = true;
        check = atts.getValue("check-interval");
        frontend = atts.getValue("frontend");
      }
    }

    public void characters (char[] ch, int start, int length) {
      if (keep)
        buf.append(ch, start, length);
    }
    
    public void endElement(String ns, String localname, String qname) {
      if (qname.equals("property")) {
        if (property.equals("check-interval"))
          config.setCheckInterval(parse(buf.toString()));
        else if (property.equals("backend"))
          config.setBackend(buf.toString());
        else
          log.warn("Unknown property: '" + property + "'");

        property = null;
        keep = false;
        buf.setLength(0);

      } else if (qname.equals("source")) {
        int interval;
        if (check == null)
          interval = config.getCheckInterval();
        else
          interval = parse(check);
        
        SyncSource src = new SyncSource(buf.toString(), interval, frontend);
        endpoint.addSource(src);
        keep = false;
        buf.setLength(0);
      }
    }

    private int parse(String num) {
      try {
        return Integer.parseInt(num.trim());
      } catch (NumberFormatException e) {
        log.error("Couldn't parse check-interval: '" + num + "'");
        return 5; // just some random, non-destructive number
      }
    }
  }
}
