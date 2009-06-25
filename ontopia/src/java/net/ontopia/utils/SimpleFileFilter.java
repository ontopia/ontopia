// $Id: SimpleFileFilter.java,v 1.3 2004/10/15 09:07:40 ian Exp $

package net.ontopia.utils;

import java.io.File;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

/**
 * INTERNAL:
 * Description: a simple file filter
 */

public class SimpleFileFilter extends FileFilter {

  private Set extensions;
  private String description;

  public SimpleFileFilter(String description) {

    this.description = description;
    this.extensions = new CompactHashSet();
  }

  public SimpleFileFilter(String description, String extension) {

    this.description = description;
    this.extensions = new CompactHashSet();
    addExtension(extension);
  }

  public SimpleFileFilter(String description, String ext1, String ext2) {

    this.description = description;
    this.extensions = new CompactHashSet();
    addExtension(ext1);
    addExtension(ext2);
  }

  public void addExtension(String extension) {

    extensions.add(extension.toLowerCase());
  }

  public String getDescription() {

    return description;
  }

  public boolean accept(File f) {

    if (f.isDirectory()) return true;

    String name = f.getName();
    int pos = name.lastIndexOf('.');
    return pos != -1
        && extensions.contains(name.substring(pos + 1).toLowerCase());
  }
}
