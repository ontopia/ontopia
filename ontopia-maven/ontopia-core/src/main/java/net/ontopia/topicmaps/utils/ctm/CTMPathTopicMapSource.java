
// $Id: CTMPathTopicMapSource.java,v 1.2 2009/02/12 11:52:17 lars.garshol Exp $

package net.ontopia.topicmaps.utils.ctm;

import java.net.URL;
import net.ontopia.topicmaps.entry.*;
import net.ontopia.infoset.core.LocatorIF;
import java.io.FileFilter;

/**
 * INTERNAL: Source that locates CTM topic map files in a directory on
 * the file system.
 * @since 4.0.5
 */
public class CTMPathTopicMapSource extends AbstractPathTopicMapSource {

  public CTMPathTopicMapSource() {
  }

  public CTMPathTopicMapSource(String path, String suffix) {
    super(path, suffix);
  }

  /**
   * INTERNAL: Constructor that takes the file directory and a file filter.
   *   
   * @param path the path to search for TopicMaps
   * @param filter a java.io.FileFilter to filter the specified path
   *
   * @since 4.0.5
   */
  public CTMPathTopicMapSource(String path, FileFilter filter) {
    super(path, filter);
  }

  protected TopicMapReferenceIF createReference(URL url, String id, String title,
                                                LocatorIF base) {
    CTMTopicMapReference ref = new CTMTopicMapReference(url, id, title, base);
    ref.setDuplicateSuppression(duplicate_suppression);
    ref.setSource(this);
    return ref;
  }
}
