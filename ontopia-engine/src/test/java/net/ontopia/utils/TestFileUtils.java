/*
 * #!
 * Ontopia Engine
 * #-
 * Copyright (C) 2001 - 2013 The Ontopia Project
 * #-
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * !#
 */

package net.ontopia.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class TestFileUtils {
	
  public static final String testdataInputRoot = "net/ontopia/testdata/";
  private static String testdataOutputRoot = null;

  public static void verifyDirectory(String dir) {
    File thedir = new File(dir);
    if (!thedir.exists()) {
      thedir.mkdirs();
    }
  }

  public static void verifyDirectory(String base, String dir) {
    File thedir = new File(base + File.separator + dir);
    if (!thedir.exists()) {
      thedir.mkdirs();
    }
  }

  public static void verifyDirectory(String base, String sub1, String sub2) {
    File thedir = new File(base + File.separator + sub1 + File.separator +
                           sub2);
    if (!thedir.exists()) {
      thedir.mkdirs();
    }
  }
  
  public static File getOutputDirectory(String... path) {
    File out = new File(getTestdataOutputDirectory());
    for (String part : path) {
      out = new File(out, part);
    }
    out.mkdirs();
    return out;
  }

  public static List<Object[]> getTestInputURLs(String... pathParts) {
    return getTestInputURLs(null, pathParts);
  }
  public static List<Object[]> getFilteredTestInputURLs(String extension, String... pathParts) {
    return getTestInputURLs(new ResourcesDirectoryReader.FilenameExtensionFilter(extension), pathParts);
  }
  public static List<Object[]> getTestInputURLs(ResourcesFilterIF filter, String... pathParts) {
    String path = testdataInputRoot;
    for (String part : pathParts) {
      path += part + File.separator;
    }
    ResourcesDirectoryReader directoryReader = (filter == null ? new ResourcesDirectoryReader(path) : new ResourcesDirectoryReader(path, filter));
    Collection<Object[]> collected = org.apache.commons.collections4.CollectionUtils.collect(directoryReader.getResources(), new Transformer<URL, Object[]>() {
      @Override
      public Object[] transform(URL input) {
        Object[] o = new Object[2];
        o[0] = input;
        o[1] = input.getFile().substring(input.getFile().lastIndexOf("/") + 1);
        return o;
      }
    });
    return new ArrayList<>(collected);
  }
  
  public static List<String[]> getTestInputFiles(String baseDirectory,
                                       String subDirectory, String filter) {
    return getTestInputFiles(baseDirectory + "/" + subDirectory, filter);
  }

  public static List<String[]> getTestInputFiles(String baseDirectory,
                                       String subDirectory,
                                       ResourcesFilterIF filter) {
    return getTestInputFiles(baseDirectory + "/" + subDirectory, filter);
  }

  public static List<String[]> getTestInputFiles(String directory, String filter) {
    String resourcesDirectory = testdataInputRoot + directory;
    ResourcesDirectoryReader directoryReader = new ResourcesDirectoryReader(resourcesDirectory, filter);
    return getTestInputFiles(directoryReader, resourcesDirectory);
  }

  public static List<String[]> getTestInputFiles(String directory, ResourcesFilterIF filter) {
    String resourcesDirectory = testdataInputRoot + directory;
    ResourcesDirectoryReader directoryReader =
      new ResourcesDirectoryReader(resourcesDirectory, filter);
    return getTestInputFiles(directoryReader, resourcesDirectory);
  }

  public static List<String[]> getTestInputFiles(ResourcesDirectoryReader directoryReader,
                                       String resourcesDirectory) {
    Collection<String> resources = directoryReader.getResourcesAsStrings();
    if (resources.isEmpty()) {
      throw new RuntimeException("No resources found in directory " +
                                 resourcesDirectory);
    }
    List<String[]> tests = new ArrayList<String[]>();
    for (String resource : resources) {
      int slashPos = resource.lastIndexOf('/') + 1;
      String root = resource.substring(0, slashPos);
      String filename = resource.substring(slashPos);
      tests.add(new String[] {root, filename});
    }
    return tests;
  }

  public static String getTestInputFile(String directory, String filename) {
    return "classpath:" + testdataInputRoot + directory + "/" + filename;
  }

  public static URL getTestInputURL(String... path) throws FileNotFoundException {
    return getTestInputURL(testdataInputRoot + StringUtils.join(path, "/"));
  }

  public static URL getTestInputURL(String resource) throws FileNotFoundException {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resource.startsWith("classpath:") ? resource.substring("classpath:".length()) : resource);
    if (url == null) {
      throw new FileNotFoundException("Test resource " + resource + " not found");
    }
    return url;
  }

  public static String getTestInputFile(String directory, String subDirectory,
                                        String filename) {
    return getTestInputFile(directory + "/" + subDirectory, filename);
  }

  public static String getTestInputFile(String directory, String subDirectory,
                                        String subSubDirectory,
                                        String filename) {
    return getTestInputFile(directory + "/" + subDirectory + "/" + subSubDirectory, filename);
  }

  private static File getTransferredTestInputFile(String filein, File fileout)
    throws IOException, FileNotFoundException {
    if (fileout.exists()) {
      // file has already been transferred, return as is
      return fileout;
    }
    // transfer test data from resource to file
    InputStream streamin = StreamUtils.getInputStream(filein);
    FileOutputStream streamout = new FileOutputStream(fileout);
    IOUtils.copy(streamin, streamout);
    streamout.close();
    streamin.close();
    return fileout;
  }

  public static File getTransferredTestInputFile(String directory,
                                                 String filename)
    throws IOException, FileNotFoundException {
    return getTransferredTestInputFile(
      getTestInputFile(directory, filename),
      getTestOutputFile(directory, filename));
  }

  public static File getTransferredTestInputFile(String directory,
                                                 String subDirectory,
                                                 String filename)
    throws IOException, FileNotFoundException {
    return getTransferredTestInputFile(
      getTestInputFile(directory, subDirectory, filename),
      getTestOutputFile(directory, subDirectory, filename));
  }

  public static File getTransferredTestInputFile(String directory,
                                                 String subDirectory,
                                                 String subSubDirectory,
                                                 String filename)
    throws IOException, FileNotFoundException {
    return getTransferredTestInputFile(
      getTestInputFile(directory, subDirectory, subSubDirectory, filename),
      getTestOutputFile(directory, subDirectory, subSubDirectory, filename));
  }

  public static void transferTestInputDirectory(String directory)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory), directory);
  }

  public static void transferTestInputDirectory(String directory,
                                                boolean searchSubdirectories)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories), directory);
  }

  public static void transferTestInputDirectory(String directory, String filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, filter), directory);
  }

  public static void transferTestInputDirectory(String directory,
                                                boolean searchSubdirectories,
                                                String filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories, filter), directory);
  }

  public static void transferTestInputDirectory(String directory,
                                                ResourcesFilterIF filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, filter), directory);
  }

  public static void transferTestInputDirectory(String directory,
                                                boolean searchSubdirectories,
                                                ResourcesFilterIF filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories, filter), directory);
  }

  public static void transferTestInputDirectory(ResourcesDirectoryReader directoryReader, String path) throws IOException {
    for (URL resource : directoryReader.getResources()) {
      String relative = resource.getFile();
      relative = relative.substring(relative.lastIndexOf(path));
      File file = new File(new File(getTestdataOutputDirectory()), relative);
      file.getParentFile().mkdirs();
      try (InputStream in = resource.openStream(); OutputStream out = new FileOutputStream(file)) {
        IOUtils.copy(in, out);
      }
    }
  }

  public static File getTestOutputFile(String directory, String filename) {
    verifyDirectory(getTestdataOutputDirectory(), directory);
    return new File(getTestdataOutputDirectory() + File.separator + directory + File.separator + filename);
  }

  public static File getTestOutputFile(String directory, String subDirectory,
                                       String filename) {
    return getTestOutputFile(directory + File.separator + subDirectory, filename);
  }

  public static File getTestOutputFile(String directory, String subDirectory,
                                       String subSubDirectory,
                                       String filename) {
    return getTestOutputFile(directory + File.separator + subDirectory +
                             File.separator + subSubDirectory, filename);
  }

  /**
   * Returns the folder used for test output files
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
  
  /**
    * INTERNAL: Compares the contents of a file and a resource that will be loaded from classpath
    */
  public static boolean compareFileToResource(String fileName, String resourceName) throws IOException {
    try (FileInputStream in1 = new FileInputStream(fileName); InputStream in2 = StreamUtils.getInputStream(resourceName)) {
      return IOUtils.contentEquals(in1, in2);
    }
  }

  /**
    * INTERNAL: Compares the contents of a file and a resource that will be loaded from classpath
    */
  public static boolean compareFileToResource(File file, String resourceName) throws IOException {
    try (FileInputStream in1 = new FileInputStream(file); InputStream in2 = StreamUtils.getInputStream(resourceName)) {
      return IOUtils.contentEquals(in1, in2);
    }
  }
}
