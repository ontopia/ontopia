// $Id: DiskPreloader.java,v 1.7 2004/11/29 19:19:53 grove Exp $

package net.ontopia.infoset.utils;

import java.io.*;
import java.net.URL;
import net.ontopia.infoset.core.*;
import net.ontopia.infoset.impl.basic.GenericLocator;

import org.apache.log4j.*;

/**
 * INTERNAL: A preloader that stores its preloaded documents on the file
 * system.<p>
 *
 * The preloader also implements LocatorReaderFactoryIF because it is
 * capable of creating readers from the locators returned by its
 * preload method.<p>
 *
 * This class is able to cache the result of preloaded
 * locators. Caching is on by default. The caching is useful to avoid
 * duplicate preloads of identical locators.<p>
 *
 * A URLLocatorReaderFactory is used by default if not specified in
 * the constructor.<p>
 */

public class DiskPreloader implements PreloaderIF, LocatorReaderFactoryIF {

  // Define a logging category.
  static Logger log = Logger.getLogger(DiskPreloader.class.getName());

  static String NOTATION = "RFILE";

  protected LocatorReaderFactoryIF lrf;
  protected String preloader_path;
  protected boolean force_download = false;

  public DiskPreloader(String preloader_path) {
    this(preloader_path, new URLLocatorReaderFactory());
  }
  
  public DiskPreloader(String preloader_path, LocatorReaderFactoryIF lrf) {
    this.preloader_path = preloader_path;
    this.lrf = lrf;
    
    // Create temporary directory
    new File(preloader_path).mkdir();
  }

  /**
   * PUBLIC: Returns true if the preloader shouldn't use its internal
   * cache, but rather force a new preload. The default is
   * <code>true</code>.
   */
  public boolean getForceDownload() {
    return force_download;
  }

  /**
   * PUBLIC: Sets the force download flag.
   */
  public void setForceDownload(boolean force_download) {
    this.force_download = force_download;
  }

  /**
   * PUBLIC: Returns the directory in which the preloaded resources
   * will be stored.
   */
  public String getPreloaderPath() {
    return preloader_path;
  }

  /**
   * PUBLIC: Sets the directory in which the preloaded resources will
   * be stored.
   */
  public void setPreloaderPath(String preloader_path) {
    this.preloader_path = preloader_path;
  }
  
  protected String getFileId(LocatorIF locator) {
    return Integer.toString(locator.toString().hashCode());
  }

  protected String getFilename(String fileid) {
    return getPreloaderPath() + File.separator + fileid;
  }

  protected Reader getReader(URL url) throws IOException {
    return new InputStreamReader(url.openConnection().getInputStream());
  }

  protected LocatorIF createLocator(String fileid) {
    return new GenericLocator(NOTATION, fileid);
  }
  
  /**
   * PUBLIC: Preloads the resource pointed to by the given locator.
   *
   * @return A URL locator 
   */
  public LocatorIF preload(LocatorIF locator) throws IOException {
    
    // Create name of preloaded file
    String fileid = getFileId(locator);    
    String filename = getFilename(fileid);

    // If file already has been loaded, and we're not required to
    // download it, use the existing one.
    File file = new File(filename);
    if (file.exists() && !force_download) return createLocator(fileid);

    // Create temporary filename
    String filename_ = getPreloaderPath() + File.separator + "_" + fileid;

    // Download resource to a temporary location
    Writer writer = null;
    Reader reader = null;
    try {
      // Create url reader
      reader = lrf.createReader(locator);
      
      // Temporary storage
      writer = new FileWriter(filename_);
      
      // Read the contents of the original reader
      int c;
      while ((c = reader.read()) != -1) {
	writer.write(c);
      }
      
    } finally {
      // Close all open descriptors
      if (writer != null) writer.close();
      if (reader != null) reader.close();
    }

    // Move temporary file to a permanent location to mark it as being
    // successfully downloaded
    File file_ = new File(filename_);
    file_.renameTo(file);
    
    // Return file reader for preloaded file.
    return createLocator(fileid);
    
  }

  public boolean needsPreloading(LocatorIF locator) {

    // Create name of preloaded file
    String fileid = getFileId(locator);
    String filename = getFilename(fileid);

    // If file already has been loaded, and we're not required to
    // download it, no preloading is necessary.
    File file = new File(filename);
    if (file.exists() && !force_download)
      return false;
    else
      return true;
  }

  public Reader createReader(LocatorIF locator) throws IOException {
    File file = new File(getFilename(locator.getAddress()));
    if (!file.exists() || !NOTATION.equals(locator.getNotation()))
      throw new IOException("Unknown locator: " + locator);
    return new FileReader(file);
  }
  
}





