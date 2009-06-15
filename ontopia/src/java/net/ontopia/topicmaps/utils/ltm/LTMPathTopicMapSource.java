
// $Id: LTMPathTopicMapSource.java,v 1.11 2006/07/12 14:17:22 larsga Exp $

package net.ontopia.topicmaps.utils.ltm;

import java.net.URL;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.infoset.core.LocatorIF;
import java.io.FileFilter;

/**
 * INTERNAL: Source that locates LTM topic map files in a directory on
 * the file system.
 * @since 1.1
 */
public class LTMPathTopicMapSource extends AbstractPathTopicMapSource {

  public LTMPathTopicMapSource() {
  }

  public LTMPathTopicMapSource(String path, String suffix) {
    super(path, suffix);
  }

  /**
   * INTERNAL: Constructor that takes the file directory and a file filter.
   *   
   * @param path the path to search for TopicMaps
   * @param filter a java.io.FileFilter to filter the specified path
   *
   * @since 1.3.4
   */
  public LTMPathTopicMapSource(String path, FileFilter filter) {
    super(path, filter);
  }

  protected TopicMapReferenceIF createReference(URL url, String id, String title,
                                                LocatorIF base) {
    LTMTopicMapReference ref = new LTMTopicMapReference(url, id, title, base);
    ref.setDuplicateSuppression(duplicate_suppression);
    ref.setSource(this);
    return ref;
  }
}
