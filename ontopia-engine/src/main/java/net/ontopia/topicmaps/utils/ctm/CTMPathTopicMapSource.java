
package net.ontopia.topicmaps.utils.ctm;

import java.net.URL;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.FileOutputStream;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.*;

/**
 * INTERNAL: Source that locates CTM topic map files in a directory on
 * the file system.
 * @since 4.0.5
 */
public class CTMPathTopicMapSource extends AbstractOntopolyTopicMapSource {

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

  public TopicMapReferenceIF createReference(URL url, String id, String title,
                                             LocatorIF base) {
    CTMTopicMapReference ref = new CTMTopicMapReference(url, id, title, base);
    ref.setDuplicateSuppression(duplicate_suppression);
    ref.setSource(this);
    ref.setMaintainFulltextIndexes(maintainFulltextIndexes);
    ref.setIndexDirectory(indexDirectory);
    ref.setAlwaysReindexOnLoad(alwaysReindexOnLoad);
    return ref;
  }

  public TopicMapWriterIF getWriter(File file) throws IOException {
    throw new UnsupportedOperationException("No CTM writer exists");
  }
}
