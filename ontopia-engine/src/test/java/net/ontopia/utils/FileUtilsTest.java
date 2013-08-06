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
import java.io.IOException;
import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

  protected File testdir;
  
  public FileUtilsTest(String name) {
    super(name);
  }
  
  public void setUp() throws IOException {
    // Get test directory
    testdir = new File(TestFileUtils.getTestdataOutputDirectory());
  }

  protected void tearDown() throws IOException {
  }
  
  // ---- test cases

  public void testDeleteFile() throws IOException {
    // Create file and delete it afterwards
    File file = File.createTempFile("FILEUTILS", "TEST", testdir);
    FileUtils.deleteFile(file);

    // Create directory and attempt to delete it
    File dir = new File(testdir, "FILEUTILS_DIR");
    try {
      FileUtils.deleteFile(dir);
      fail("Was able to delete directory using FileUtils.deleteFile(File)");
    } catch (IOException e) {
      // Ignore
    }
    // Clean up
    dir.delete();    
  }
  
  public void testDeleteFileByName() throws IOException {
    // Create file and delete it afterwards
    File file = File.createTempFile("FILEUTILS", "TEST", testdir);
    FileUtils.deleteFile(file.getAbsolutePath());

    // Create directory and attempt to delete it
    File dir = new File(testdir, "FILEUTILS_DIR");
    try {
      FileUtils.deleteFile(dir.getAbsolutePath());
      fail("Was able to delete directory using FileUtils.deleteFile(String)");
    } catch (IOException e) {
      // Ignore
    }
    dir.delete();
  }
  
  public void testDeleteDirectory() throws IOException {
    // Create directory and delete it afterwards
    File dir = new File(testdir, "FILEUTILS_DIR");
    dir.mkdir();
    FileUtils.deleteDirectory(dir, false);

    // Create file and attempt to delete it
    File file = File.createTempFile("FILEUTILS", "TEST", testdir);
    try {
      FileUtils.deleteDirectory(file, false);
      fail("Was able to delete file using FileUtils.deleteDirectory(File, false)");
    } catch (IOException e) {
      // Ignore
    }
    file.delete();
  }
  
  public void testDeleteDirectoryByName() throws IOException {
    // Create directory and delete it afterwards
    File dir = new File(testdir, "FILEUTILS_DIR");    
    dir.mkdir();
    FileUtils.deleteDirectory(dir.getAbsolutePath(), false);

    // Create file and attempt to delete it
    File file = File.createTempFile("FILEUTILS", "TEST", testdir);
    try {
      FileUtils.deleteDirectory(file.getAbsolutePath(), false);
      fail("Was able to delete file using FileUtils.deleteDirectory(String, false)");
    } catch (IOException e) {
      // Ignore
    }
    file.delete();
  }
  
  public void testDelete_file() throws IOException {
    // Create file and attempt to delete it
    File file = File.createTempFile("FILEUTILS", "TEST", testdir);
    FileUtils.delete(file, false);
  }
  
  public void testDeleteRecursive_file() throws IOException {
    // Create file and attempt to delete it
    File file = File.createTempFile("FILEUTILS", "TEST", testdir);
    FileUtils.delete(file, true);
  }

  // recursive delete
  
  public void testDeleteDirectoryRecursive() throws IOException {
    File dir = createNestedDirectory();
    // This should work
    FileUtils.deleteDirectory(dir, true);
  }
  
  public void testDeleteRecursive_dir() throws IOException {
    File dir = createNestedDirectory();
    // This should work
    FileUtils.delete(dir, true);
  }

  /**
   * INTERNAL: Creates a directory with some nested directories and
   * some files.
   */
  protected File createNestedDirectory() throws IOException {
    // Structure:
    //
    // FILEUTILS_DIR1
    //   FILEUTILS_DIR2a
    //     FILEUTILS_DIR3
    //       temp_file
    //   FILEUTILS_DIR2b
    //     temp_file1
    //     temp_file2
    
    // DIR1
    File dir1 = new File(testdir, "FILEUTILS_DIR1");    
    dir1.mkdir();
    
    // DIR1/DIR2A
    File dir2a = new File(dir1, "FILEUTILS_DIR2a");    
    dir2a.mkdir();

    // DIR1/DIR2A/DIR3
    File dir3 = new File(dir2a, "FILEUTILS_DIR3");    
    dir3.mkdir();
    File file3 = File.createTempFile("FILEUTILS", "TEST", dir3);
    
    // DIR1/DIR2B
    File dir2b = new File(dir1, "FILEUTILS_DIR2b");    
    dir2b.mkdir();
    File file2b1 = File.createTempFile("FILEUTILS", "TEST", dir2b);
    File file2b2 = File.createTempFile("FILEUTILS", "TEST", dir2b);

    // Return the top level directory
    return dir1;
  }
  
}
