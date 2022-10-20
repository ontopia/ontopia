/*
 * #!
 * Ontopia Content Store
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

package net.ontopia.infoset.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: A content store implementation based on the file system.
 * It uses a two-level structure where inside the root directory is a
 * set of directories, each of which contains a fixed number of files
 * (N). A key is mapped to a file name (key modulo N) and a directory
 * name (key divided by N). The next free key is stored in a file in
 * the top directory, and keys are allocated in blocks.
 */
public class FileContentStore implements ContentStoreIF {
  public static final int FILES_PER_DIRECTORY = 1000;
  public static final int KEY_BLOCK_SIZE = 10;
  public static final int MAX_SPINS = 1000;
  public static final int SPIN_TIMEOUT = 10;

  private boolean open;
  private File store_root;
  private int last_key;
  private int end_of_key_block;
  private File key_file;
  
  public FileContentStore(File store_root) throws ContentStoreException {
    if (!store_root.canWrite()) {
      throw new ContentStoreException("Content store root directory '" +
                                      store_root.getAbsoluteFile() +
                                      "' not writable.");
    }

    // FIXME: should use java.nio.FileLock to ensure that only this
    // JVM accesses this directory. should also use some mechanism to
    // ensure that we are the only FileContentStore on this directory
    // within this JVM.
    //
    // UPDATE: key file is now being protected using file locks. Still
    // need to add locks when deleting entries.

    // set up members
    this.store_root = store_root;
    this.open = true;
    this.key_file = new File(store_root, "keyfile.txt");

    // initialize
    allocateNewBlock();
  }

  // --- ContentStoreIF implementation
  
  @Override
  public synchronized boolean containsKey(int key) throws ContentStoreException {
    checkOpen();
    return getFileForKey(key).exists();
  }

  @Override
  public synchronized ContentInputStream get(int key) throws ContentStoreException {
    checkOpen();
    File file = getFileForKey(key);

    try {
      return new ContentInputStream(new FileInputStream(file), (int) file.length());
    } catch (FileNotFoundException e) {
      throw new ContentStoreException("No entry in content store for key " + key +
                                      "; file " + file.getAbsoluteFile() + " not " +
                                      "found.");
    }
  }
  
  @Override
  public int add(ContentInputStream data) throws ContentStoreException {
    return add(data, data.getLength());
  }
  
  @Override
  public synchronized int add(InputStream data, int length)
    throws ContentStoreException {
    checkOpen();
    int key = getNewKey();
    File file = getFileForKey(key);
    if (file.exists()) {
      throw new ContentStoreException("Content store corrupted: file already " +
                                      "exists for key " + key + ".");
    }

    try {
      // verify that container directory exists
      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdir();
      }

      // store data
      OutputStream out = new FileOutputStream(file);
      IOUtils.copy(data, out);
      out.close();
    } catch (IOException e) {
      throw new ContentStoreException("Error writing data to content store.", e);
    }

    // integrity check
    if (file.length() != length) {
      throw new ContentStoreException("Stored entry for key " + key + " of wrong " +
                                      "size. Given length was " + length + ", but " +
                                      "resulting entry was " + file.length());
    }

    return key;
  }

  @Override
  public synchronized boolean remove(int key) throws ContentStoreException {
    checkOpen();
    File file = getFileForKey(key);
    return file.delete();
  }

  @Override
  public synchronized void close() throws ContentStoreException {
    checkOpen();
    open = false;
  }

  // --- Internal helpers

  private void checkOpen() throws ContentStoreException {
    if (!open) {
      throw new ContentStoreException("Content store on " + store_root +  "not open");
    }
  }

  private File getFileForKey(int key) {
    int dirpart = key / FILES_PER_DIRECTORY;
    int filepart = key % FILES_PER_DIRECTORY;
    return new File(store_root, "" + dirpart + File.separator + filepart);
  }

  private int getNewKey() throws ContentStoreException {
    if (last_key == end_of_key_block) {
      allocateNewBlock();
    }

    last_key++;
    return last_key;
  }

  private void allocateNewBlock() throws ContentStoreException {
    RandomAccessFile out = null;
    boolean exception_thrown = false;
    try {
      out = new RandomAccessFile(key_file, "rws");

      for (int i=0; i < MAX_SPINS; i++) {
        // acquire exclusive lock
        FileLock l = out.getChannel().tryLock();
  
        if (l == null) {
          // wait a little before trying again
          try {
            Thread.sleep(SPIN_TIMEOUT);
          } catch (InterruptedException e) {
          }
          continue;
    
        } else {
          try {
            // allocate new key
            int old_key;
            int new_key;
            String content = null;
      
            if (out.length() == 0) {
              old_key = 0;
              new_key = old_key + KEY_BLOCK_SIZE;
        
            } else {
              try {
                content = out.readUTF();
                old_key = Integer.parseInt(content);
                new_key = old_key + KEY_BLOCK_SIZE;
              } catch (NumberFormatException e) {
                if (content.length() > 100) {
                  content = content.substring(0, 100) + "...";
                }
                throw new ContentStoreException("Content store key file corrupted. Contained: '" + content + "'");
              }
            }
      
            // truncate key file and write out new key
            out.seek(0);
            out.writeUTF(Integer.toString(new_key));
      
            end_of_key_block = new_key;
            last_key = old_key;
            return;
          } finally {
            // release file lock
            try {
              l.release();
            } catch (Throwable t) {
              throw new ContentStoreException("Could not release key file lock.", t);
            }
          }
        }
      }
      throw new ContentStoreException("Block allocation timed out.");

    } catch (ContentStoreException e) {
      exception_thrown = true;
      throw e;

    } catch (Throwable t) {
      exception_thrown = true;
      throw new ContentStoreException(t);

    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
          if (!exception_thrown) {
            throw new ContentStoreException ("Problems occurred when closing content store.", e);
          }
        }
      }
    }
  }


}
