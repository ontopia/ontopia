
package net.ontopia.topicmaps.utils.ltm;

import java.net.URL;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.FileOutputStream;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.*;

/**
 * INTERNAL: Source that locates LTM topic map files in a directory on
 * the file system.
 * @since 1.1
 */
public class LTMPathTopicMapSource extends AbstractOntopolyTopicMapSource {

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

  public TopicMapReferenceIF createReference(URL url, String id, String title,
                                             LocatorIF base) {
    LTMTopicMapReference ref = new LTMTopicMapReference(url, id, title, base);
    ref.setDuplicateSuppression(duplicate_suppression);
    ref.setSource(this);
    ref.setMaintainFulltextIndexes(maintainFulltextIndexes);
    ref.setIndexDirectory(indexDirectory);
    ref.setAlwaysReindexOnLoad(alwaysReindexOnLoad);
    return ref;
  }

  public TopicMapWriterIF getWriter(File file) throws IOException {
    return new LTMTopicMapWriter(new FileOutputStream(file));
  }
}
