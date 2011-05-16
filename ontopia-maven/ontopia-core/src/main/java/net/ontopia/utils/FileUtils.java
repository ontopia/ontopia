
// $Id: FileUtils.java,v 1.17 2008/11/03 12:26:44 lars.garshol Exp $

package net.ontopia.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;

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
    * INTERNAL: Compares the contents of a file and a resource that will be loaded from classpath
    */
  public static boolean compareFileToResource(String fileName, String resourceName) throws IOException {
    return StreamUtils.compareAndClose(new FileInputStream(fileName), StreamUtils.getInputStream(resourceName));
  }

  /**
    * INTERNAL: Compares the contents of a file and a resource that will be loaded from classpath
    */
  public static boolean compareFileToResource(File file, String resourceName) throws IOException {
    return StreamUtils.compareAndClose(new FileInputStream(file), StreamUtils.getInputStream(resourceName));
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
  

  // File convenience methods

  public static void verifyDirectory(String dir) {
    File thedir = new File(dir);
    if (!thedir.exists())
      thedir.mkdirs();
  }

  public static void verifyDirectory(String base, String dir) {
    File thedir = new File(base + File.separator + dir);
    if (!thedir.exists())
      thedir.mkdirs();
  }

  public static void verifyDirectory(String base, String sub1, String sub2) {
    File thedir = new File(base + File.separator + sub1 + File.separator +
                           sub2);
    if (!thedir.exists())
      thedir.mkdirs();
  }



  public static final String testdataInputRoot = "net/ontopia/testdata/";
  private static String testdataOutputRoot = null;

  public static List getTestInputFiles(String baseDirectory, String subDirectory, String filter) {
    return getTestInputFiles(baseDirectory + "/" + subDirectory, filter);
  }
  public static List getTestInputFiles(String baseDirectory, String subDirectory, ResourcesFilterIF filter) {
    return getTestInputFiles(baseDirectory + "/" + subDirectory, filter);
  }
  public static List getTestInputFiles(String directory, String filter) {
    String resourcesDirectory = testdataInputRoot + directory;
    ResourcesDirectoryReader directoryReader = new ResourcesDirectoryReader(resourcesDirectory, filter);
    return getTestInputFiles(directoryReader, resourcesDirectory);
  }
  public static List getTestInputFiles(String directory, ResourcesFilterIF filter) {
    String resourcesDirectory = testdataInputRoot + directory;
    ResourcesDirectoryReader directoryReader = new ResourcesDirectoryReader(resourcesDirectory, filter);
    return getTestInputFiles(directoryReader, resourcesDirectory);
  }
  public static List getTestInputFiles(ResourcesDirectoryReader directoryReader, String resourcesDirectory) {
    Set<String> resources = directoryReader.getResources();
    if (resources.size() == 0) throw new RuntimeException("No resources found in directory " + resourcesDirectory);
    List<String[]> tests = new ArrayList<String[]>();
    for (String resource : resources) {
      int slashPos = resource.lastIndexOf("/") + 1;
      String root = resource.substring(0, slashPos);
      String filename = resource.substring(slashPos);
      tests.add(new String[] {root, filename});
    }
    return tests;
  }

  public static String getTestInputFile(String directory, String filename) {
    return "classpath:" + testdataInputRoot + directory + "/" + filename;
  }
  public static String getTestInputFile(String directory, String subDirectory, String filename) {
    return getTestInputFile(directory + "/" + subDirectory, filename);
  }
  public static String getTestInputFile(String directory, String subDirectory, String subSubDirectory, String filename) {
    return getTestInputFile(directory + "/" + subDirectory + "/" + subSubDirectory, filename);
  }

  private static File getTransferredTestInputFile(String filein, File fileout) throws IOException, FileNotFoundException {
    if (fileout.exists()) {
      // file has already been transferred, return as is
      return fileout;
    }
    // transfer test data from resource to file
    InputStream streamin = StreamUtils.getInputStream(filein);
    FileOutputStream streamout = new FileOutputStream(fileout);
    StreamUtils.transfer(streamin, streamout);
    streamout.close();
    streamin.close();
    return fileout;
  }
  public static File getTransferredTestInputFile(String directory, String filename) throws IOException, FileNotFoundException {
    return getTransferredTestInputFile(
      getTestInputFile(directory, filename),
      getTestOutputFile(directory, filename));
  }
  public static File getTransferredTestInputFile(String directory, String subDirectory, String filename) throws IOException, FileNotFoundException {
    return getTransferredTestInputFile(
      getTestInputFile(directory, subDirectory, filename),
      getTestOutputFile(directory, subDirectory, filename));
  }
  public static File getTransferredTestInputFile(String directory, String subDirectory, String subSubDirectory, String filename) throws IOException, FileNotFoundException {
    return getTransferredTestInputFile(
      getTestInputFile(directory, subDirectory, subSubDirectory, filename),
      getTestOutputFile(directory, subDirectory, subSubDirectory, filename));
  }

  public static void transferTestInputDirectory(String directory) throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory));
  }
  public static void transferTestInputDirectory(String directory, boolean searchSubdirectories) throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories));
  }
  public static void transferTestInputDirectory(String directory, String filter) throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, filter));
  }
  public static void transferTestInputDirectory(String directory, boolean searchSubdirectories, String filter) throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories, filter));
  }
  public static void transferTestInputDirectory(String directory, ResourcesFilterIF filter) throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, filter));
  }
  public static void transferTestInputDirectory(String directory, boolean searchSubdirectories, ResourcesFilterIF filter) throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories, filter));
  }
  public static void transferTestInputDirectory(ResourcesDirectoryReader directoryReader) throws IOException {
    Set<String> resources = directoryReader.getResources();
    for (String resource : resources) {
      int slashPos = resource.lastIndexOf("/") + 1;
      String root = resource.substring(testdataInputRoot.length(), slashPos);
      String filename = resource.substring(slashPos);
      getTransferredTestInputFile(root, filename);
    }
  }

  public static File getTestOutputFile(String directory, String filename) {
    verifyDirectory(getTestdataOutputDirectory(), directory);
    return new File(getTestdataOutputDirectory() + File.separator + directory + File.separator + filename);
  }
  public static File getTestOutputFile(String directory, String subDirectory, String filename) {
    return getTestOutputFile(directory + File.separator + subDirectory, filename);
  }
  public static File getTestOutputFile(String directory, String subDirectory, String subSubDirectory, String filename) {
    return getTestOutputFile(directory + File.separator + subDirectory + File.separator + subSubDirectory, filename);
  }

  /**
   * Returns the folder used for test output files
   * @return the folder used for test output files
   */
  public static String getTestdataOutputDirectory() {
    if (testdataOutputRoot == null) {
      testdataOutputRoot = System.getProperty("net.ontopia.test.root");
      // Fall back to the user home directory
      if (testdataOutputRoot == null) {
        testdataOutputRoot = System.getProperty("user.dir") + File.separator + "target" + File.separator + "test-data" + File.separator;
      }
      // Complain if the directory couldn't be found.
      if (testdataOutputRoot == null) {
        throw new OntopiaRuntimeException("Could not find test root directory."
                + " Please set the 'net.ontopia.test.root'"
                + " system property.");
      }

      // verify the root
      verifyDirectory(testdataOutputRoot);

    }
    return testdataOutputRoot;
  }

}
