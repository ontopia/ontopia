
package net.ontopia.topicmaps.cmdlineutils.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;

public class TopicMapReader {

  static public TopicMapIF getTopicMap(String infile) 
    throws OntopiaRuntimeException, IOException, MalformedURLException {
    if (infile.endsWith(".xtm")) return new XTMTopicMapReader(infile).read();
    if (infile.endsWith(".ltm")) return new LTMTopicMapReader(infile).read();
    throw new OntopiaRuntimeException("Error with infile: suffix not supported");
  }

}





