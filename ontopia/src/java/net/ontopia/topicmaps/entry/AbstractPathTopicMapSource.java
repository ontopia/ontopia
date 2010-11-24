
// $Id: AbstractPathTopicMapSource.java,v 1.48 2007/08/29 12:56:03 geir.gronmo Exp $

package net.ontopia.topicmaps.entry;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;

/**
 * INTERNAL: Abstract class implementing TopicMapSourceIF; locates
 * topic map file references from a given directory on the local file
 * system. Only files that match the given suffix are used.<p>
 */
public abstract class AbstractPathTopicMapSource
  implements TopicMapSourceIF, FileFilter {

  protected String id;
  protected String title;
  protected String path;
  protected String suffix;
  protected LocatorIF base_address;
  protected boolean duplicate_suppression;
  protected boolean hidden;

  protected Map<String, TopicMapReferenceIF> refmap;

  private FileFilter filter;

  public AbstractPathTopicMapSource() {
    this.filter = this;
  }

  public AbstractPathTopicMapSource(String path, String suffix) {
    this(path, suffix, null);
    this.filter = this;
  }

  public AbstractPathTopicMapSource(String path, FileFilter filter) {
    this(path, filter, null);
  }

  public AbstractPathTopicMapSource(String path, String suffix,
                                    LocatorIF base_address) {
    this.path = path;
    this.suffix = suffix;
    this.base_address = base_address;
    this.filter = this;
  }

  public AbstractPathTopicMapSource(String path, FileFilter filter,
                                    LocatorIF base_address) {
    this.path = path;
    this.base_address = base_address;
    this.filter = filter;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * INTERNAL: Gets the path in which the source locates its references.
   */
  public String getPath() {
    return path;
  }

  /**
   * INTERNAL: Sets the path in which the source locates its references.
   */
  public void setPath(String path) {
    this.path = path;
  }

  /**
   * INTERNAL: Gets the file suffix that should be used for filtering.
   */
  public String getSuffix() {
    return suffix;
  }

  /**
   * INTERNAL: Sets the file suffix that should be used for filtering.
   */
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  /**
   * INTERNAL: Gets the base locator of the topic maps retrieved from
   * the source.
   *
   * @since 1.3.2
   */
  public LocatorIF getBase() {
    return base_address;
  }

  /**
   * INTERNAL: Sets the base locator of the topic maps retrieved from
   * the source.
   *
   * @since 1.3.2
   */
  public void setBase(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /**
   * INTERNAL: Gets the base address of the topic maps retrieved from
   * the source. The notation is assumed to be 'URI'.
   *
   * @since 1.2.5
   */
  public String getBaseAddress() {
    return (base_address == null ? null : base_address.getAddress());
  }

  /**
   * INTERNAL: Sets the base address of the topic maps retrieved from
   * the source. The notation is assumed to be 'URI'.
   *
   * @since 1.2.5
   */
  public void setBaseAddress(String base_address) {
    try {
      this.base_address = new URILocator(base_address);
    } catch (MalformedURLException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  /**
   * INTERNAL: Gets the duplicate suppression flag. If the flag is
   * true duplicate suppression is to be performed when loading the
   * topic maps.
   *
   * @since 1.4.2
   */
  public boolean getDuplicateSuppression() {
    return duplicate_suppression;
  }

  /**
   * INTERNAL: Sets the duplicate suppression flag. If the flag is
   * true duplicate suppression is to be performed when loading the
   * topic maps.
   *
   * @since 1.4.2
   */
  public void setDuplicateSuppression(boolean duplicate_suppression) {
    this.duplicate_suppression = duplicate_suppression;
  }
  
  public synchronized Collection<TopicMapReferenceIF> getReferences() {
    if (refmap == null) refresh();
    return refmap.values();
  }

  public boolean supportsCreate() {
    return false;
  }

  public boolean supportsDelete() {
    return false;
  }

  public TopicMapReferenceIF createTopicMap(String name, String baseAddress) {
    throw new UnsupportedOperationException();
  }

  public synchronized void refresh() {
    if (path == null)
      throw new OntopiaRuntimeException("'path' property has not been set.");
    if ((suffix == null) && (filter==this))
      throw new OntopiaRuntimeException("'suffix' property for filter has not "+
                                        "been set");
    refmap = refreshFromFilesystem();
  }
  
  protected Map<String, TopicMapReferenceIF> refreshFromFilesystem() {
    Map<String, TopicMapReferenceIF> newmap = new HashMap<String, TopicMapReferenceIF>();
    // Initialize filter
    File directory = new File(path);
    if (!directory.exists())
      throw new OntopiaRuntimeException("Directory " + directory +
                                        " does not exist!");
    if (!directory.isDirectory())
      throw new OntopiaRuntimeException(directory + " is a file, not a directory");
    // Filter filenames
    File[] files;
    if (filter != null)
      files = directory.listFiles(filter);
    else
      files = directory.listFiles();

    // Loop over matched files.
    for (int i=0; i < files.length; i++) {
      try {
        String filename = files[i].getName();
        String id = filename;
        URL url = URIUtils.toURL(files[i]);
        TopicMapReferenceIF ref = createReference(url, id, filename);
        if (ref != null)
          newmap.put(id, ref);
        
      } catch (MalformedURLException e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    return newmap;
  }

  protected TopicMapReferenceIF createReference(URL url, String id, String title) {
    // Do not create new reference if reference is already open.
    if (refmap != null) {
      TopicMapReferenceIF ref = refmap.get(id);
      if (ref != null && ref.isOpen()) {
        // Use existing reference
        return ref;
      }
    }
    // Create new topic map reference
    return createReference(url, id, title, base_address);
  }

  
  protected abstract TopicMapReferenceIF createReference(URL url,
                                                         String id,
                                                         String title,
                                                         LocatorIF base_address);

  /**
   * INTERNAL: Returns the FileFilter used to in the specified
   * path.<p>
   *
   * @since 1.3.4
   */
  public FileFilter getFileFilter() {
    return filter;
  }

  /**
   * INTERNAL: Sets a FileFilter used to filter the files in the
   * specified path.<p>
   *
   * @param filter a java.io.FileFilter object for filtering the files
   *
   * @since 1.3.4
   */
  public void setFileFilter(FileFilter filter) {
    this.filter = filter;
  }

  /**
   * INTERNAL: A file filter method implementation. It accepts a file
   * if it is not a directory and the filename ends with the specified
   * suffix.
   */
  public boolean accept(File file) {
    // default FileFilter implementation
    return (!file.isDirectory() && file.getName().endsWith(suffix));
  }

  public boolean getHidden() {
    return hidden;
  }

  public void setHidden(boolean hidden) {
    this.hidden = hidden;
  }

}
