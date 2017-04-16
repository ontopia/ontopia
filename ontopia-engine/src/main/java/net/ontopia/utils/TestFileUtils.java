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
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.ontopia.utils.ResourcesDirectoryReader.ResourcesFilterIF;

public class TestFileUtils {
	
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
    Set<String> resources = directoryReader.getResources();
    if (resources.size() == 0)
      throw new RuntimeException("No resources found in directory " +
                                 resourcesDirectory);
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

  public static URL getTestInputURL(String directory, String filename) {
    return getTestInputURL(testdataInputRoot + directory + "/" + filename);
  }

  public static URL getTestInputURL(String resource) {
    URL url = Thread.currentThread().getContextClassLoader().getResource(resource.startsWith("classpath:") ? resource.substring("classpath:".length()) : resource);
    if (url == null) throw new OntopiaRuntimeException("Test resource " + resource + " not found");
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
    StreamUtils.transfer(streamin, streamout);
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
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory));
  }

  public static void transferTestInputDirectory(String directory,
                                                boolean searchSubdirectories)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories));
  }

  public static void transferTestInputDirectory(String directory, String filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, filter));
  }

  public static void transferTestInputDirectory(String directory,
                                                boolean searchSubdirectories,
                                                String filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, searchSubdirectories, filter));
  }

  public static void transferTestInputDirectory(String directory,
                                                ResourcesFilterIF filter)
    throws IOException {
    transferTestInputDirectory(new ResourcesDirectoryReader(testdataInputRoot + directory, filter));
  }

  public static void transferTestInputDirectory(String directory,
                                                boolean searchSubdirectories,
                                                ResourcesFilterIF filter)
    throws IOException {
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

}
