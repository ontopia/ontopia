
// $Id: AbstractCanonicalTestCase.java,v 1.7 2003/10/20 13:12:39 larsga Exp $

package net.ontopia.topicmaps.xml;

import java.io.*;
import net.ontopia.topicmaps.test.*;

/**
 * INTERNAL: Abstract test case implementation that is reused by the
 * test case generators for the XTM exporter and the XTM importer.
 */

public abstract class AbstractCanonicalTestCase extends AbstractTopicMapTestCase {

  public AbstractCanonicalTestCase(String name) {
    super(name);
  }

}
