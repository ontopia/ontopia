
package net.ontopia.topicmaps.utils.sdshare;

import java.io.Writer;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import org.xml.sax.helpers.AttributeListImpl;
import net.ontopia.xml.PrettyPrinter;
import net.ontopia.infoset.core.LocatorIF;

/**
 * INTERNAL: Utility class using SAX DocumentHandler events to produce
 * an Atom output.
 */
public class AtomWriter {
  private PrettyPrinter out;
  private AttributeListImpl atts;
  private SimpleDateFormat format;

  public AtomWriter(Writer out) {
    this.out = new PrettyPrinter(out, null); // null = no XML decl
    this.atts = new AttributeListImpl();
    this.format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    format.setTimeZone(TimeZone.getTimeZone("Z"));
  }

  public void startFeed(String title, long updated, String id) {
    out.startDocument();
    atts.addAttribute("xmlns", "CDATA", "http://www.w3.org/2005/Atom");
    atts.addAttribute("xmlns:sdshare", "CDATA",
                      "http://www.egovpt.org/sdshare");
    out.startElement("feed", atts);

    atts.clear();
    out.startElement("title", atts);
    out.characters(title.toCharArray(), 0, title.length());
    out.endElement("title");

    atts.clear();
    out.startElement("updated", atts);
    String content = format.format(updated);
    out.characters(content.toCharArray(), 0, content.length());
    out.endElement("updated");
    
    atts.clear();
    out.startElement("author", atts);
    content = "Ontopia SDshare server";
    out.characters(content.toCharArray(), 0, content.length());
    out.endElement("author");

    atts.clear();
    out.startElement("id", atts);
    out.characters(id.toCharArray(), 0, id.length());
    out.endElement("id");    
  }

  public void addServerPrefix(String prefix) {
    atts.clear();
    out.startElement("sdshare:ServerSrcLocatorPrefix", atts);
    out.characters(prefix.toCharArray(), 0, prefix.length());
    out.endElement("sdshare:ServerSrcLocatorPrefix");    
  }

  public void startEntry(String title, String id, long updated) {
    atts.clear();
    out.startElement("entry", atts);

    atts.clear();
    out.startElement("title", atts);
    out.characters(title.toCharArray(), 0, title.length());
    out.endElement("title");
    
    atts.clear();
    out.startElement("updated", atts);
    String content = format.format(updated);
    out.characters(content.toCharArray(), 0, content.length());
    out.endElement("updated");

    atts.clear();
    out.startElement("id", atts);
    out.characters(id.toCharArray(), 0, id.length());
    out.endElement("id");    
  }

  public void addLink(String href) {
    addLink(href, null, null);
  }
  
  public void addLink(String href, String type, String rel) {
    atts.clear();
    atts.addAttribute("href", "CDATA", href);
    if (type != null)
      atts.addAttribute("type", "CDATA", type);
    if (rel != null)
      atts.addAttribute("rel", "CDATA", rel);
    out.startElement("link", atts);
    out.endElement("link");    
  }

  public void addTopicSI(LocatorIF si) {
    atts.clear();
    out.startElement("sdshare:TopicSI", atts);
    String content = si.getExternalForm();
    out.characters(content.toCharArray(), 0, content.length());
    out.endElement("sdshare:TopicSI");    
  }
  
  public void endEntry() {
    out.endElement("entry");    
  }

  public void endFeed() {
    out.endElement("feed");
    out.endDocument();
  }
}