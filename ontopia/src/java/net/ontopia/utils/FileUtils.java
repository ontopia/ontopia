
// $Id: FileUtils.java,v 1.17 2008/11/03 12:26:44 lars.garshol Exp $

package net.ontopia.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * INTERNAL: Class that contains useful file operation methods.
 *
 * @since 1.3
 */
public class FileUtils {
  
  /**
   * INTERNAL: Returns true if file exists.
   */
  public static boolean fileExists(String filename) {
    if (filename == null) return false;
    return new File(filename).exists();
  }
  
  /**
   * INTERNAL: Deletes the file with the given name.
   */
  public static void deleteFile(String filename) throws IOException {
    deleteFile(new File(filename));
  }
  
  /**
   * INTERNAL: Deletes the file.
   */
  public static void deleteFile(File file) throws IOException {
    // Check to see if file exists.
    if (!file.exists())
      throw new IOException("The file '" + file + "' does not exist.");
    // Complain if file is not a normal file
    if (!file.isFile())
      throw new IOException("'" + file + "' is not a file.");    
    if (!file.delete())
      throw new IOException("Couldn't delete file '" + file + "'.");
  }
   
  /**
   * INTERNAL: Deletes the directory with the given name, recursively
   * if specified.
   */
  public static void deleteDirectory(String dirname, boolean recursive) throws IOException {
    deleteDirectory(new File(dirname), recursive);
  }
  
  /**
   * INTERNAL: Deletes the directory, recursively if specified.
   */
  public static void deleteDirectory(File dir, boolean recursive) throws IOException {
    // Check to see if directory exists.
    if (!dir.exists())
      throw new IOException("The directory '" + dir + "' does not exist.");
    // Complain if file is not a directory
    if (!dir.isDirectory())
      throw new IOException("'" + dir + "' is not a directory.");    
    
    if (recursive) {
      // Get a list of the files in the directory
      File[] dirfiles = dir.listFiles();
      // Note: listFiles() returns null if not directory or exception occurred.
      if (dirfiles != null) {
        for (int i=0; i < dirfiles.length; i++) {
          // Delete file or directory recursively
          delete(dirfiles[i], true);
        }
      }
    }
    
    // Delete the directory (since it should now be empty)
    if (!dir.delete())
      throw new IOException("Couldn't delete directory '" + dir + "'.");
  }
  
  /**
   * INTERNAL: Deletes the file or directory with the given name,
   * recursively if specified.
   */
  public static void delete(String file_or_dirname, boolean recursive) throws IOException {
    delete(new File(file_or_dirname), recursive);
  }
  
  /**
   * INTERNAL: Deletes the file or directory, recursively if
   * specified.
   */
  public static void delete(File file_or_dir, boolean recursive) throws IOException {
    // Check to see if file exists.
    if (!file_or_dir.exists())
      throw new IOException("The File '" + file_or_dir + "' does not exist.");
    
    if (recursive) {
      // Get a list of the files in the directory
      File[] dirfiles = file_or_dir.listFiles();
      // Note: listFiles() returns null if not directory or exception occurred.
      if (dirfiles != null) {
        for (int i=0; i < dirfiles.length; i++) {
          // Delete file or directory recursively
          delete(dirfiles[i], true);
        }
      }
    }
    
    // Delete the file (if it is a directory it should now be empty)
    if (!file_or_dir.delete())
      throw new IOException("Couldn't delete File '" + file_or_dir + "'.");
  }

  /**
   * INTERNAL: Compares the two files for equality.
   */
  public static boolean compare(String file1, String file2) throws IOException {
    return compare(new File(file1), new File(file2));
  }
  
  /**
   * INTERNAL: Compares the two files for equality.
   */
  public static boolean compare(File file1, File file2) throws IOException {
    InputStream s1 = new BufferedInputStream(new FileInputStream(file1));
    try {
      InputStream s2 = new BufferedInputStream(new FileInputStream(file2));
      try  {
        return StreamUtils.compare(s1, s2);
      } finally {
        s2.close();
      }
    } finally {
      s1.close();
    }
  }

  /**
   * INTERNAL: Copies a file's content to another file.
   */
  public static void copyFile(File source, File target) throws IOException {
    InputStream istream = new BufferedInputStream(new FileInputStream(source));
    try {
      OutputStream ostream = new BufferedOutputStream(new FileOutputStream(target));
      try {
        StreamUtils.transfer(istream, ostream);
      } finally {
        ostream.close();
      }
    } finally {
      istream.close();
    }    
  }
  
}
