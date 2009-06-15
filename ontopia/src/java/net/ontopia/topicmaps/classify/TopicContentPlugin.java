
// $Id: TopicContentPlugin.java,v 1.1 2007/07/13 08:33:30 lars.garshol Exp $

package net.ontopia.topicmaps.classify;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import org.xml.sax.SAXException;
import org.xml.sax.DocumentHandler;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.xml.PrettyPrinter;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.xml.XTMFragmentExporter;

/**
 * INTERNAL: Classifier plugin which produces content from the topic
 * itself.
 */
public class TopicContentPlugin implements ClassifyPluginIF {

  public boolean isClassifiable(TopicIF topic) {
    return true; // sure, we can do this for any topic
  }

  public ClassifiableContentIF getClassifiableContent(TopicIF topic) {
    return new TopicAsContent(topic);
  }

  // ----- INTERNAL CLASS

  public static class TopicAsContent implements ClassifiableContentIF {
    private TopicIF topic;

    public TopicAsContent(TopicIF topic) {
      this.topic = topic;
    }
    
    public String getIdentifier() {
      return topic.getObjectId();
    }

    public byte[] getContent() {
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrettyPrinter pp = new PrettyPrinter(out);
        XTMFragmentExporter exporter = new XTMFragmentExporter();
        exporter.exportAll(Collections.singleton(topic).iterator(), pp);
        pp.endDocument();
        return out.toByteArray();
      } catch (SAXException e) {
        throw new OntopiaRuntimeException(e);
      } catch (IOException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
  }
}
