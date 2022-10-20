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
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.Comparator;

/**
 * INTERNAL: A utility class that analyzes a file content store
 * directory and repairs the key file. WARNING: all applications using
 * the file content store must be stopped before running this
 * application.
 */
public class RepairKeyFile {
  
  public static void main(String[] args) throws Exception {
    File cstore = new File(args[0]);
    File[] files = cstore.listFiles(new FileFilter() {
  @Override
	public boolean accept(File file) {
	  if (file.isDirectory()) {
	    String name = file.getName();
	    try {
	      Integer.parseInt(name);
	      return true;
	    } catch (NumberFormatException e) {
	      return false;
	    }	    
	  }
	  return false;
	}
      });
    
    Arrays.sort(files, new Comparator<File>() {
      @Override
      public int compare(File f1, File f2) {
        int i1 = Integer.parseInt(f1.getName());
        int i2 = Integer.parseInt(f2.getName());

        return (i1 > i2 ? -1 : (i1 < i2 ? 1 : 0));
      }
    });

    int dirval = 0;
    if (files.length > 0) {
      File maxdir = files[0];
      dirval = Integer.parseInt(maxdir.getName());
    }
    // calculating existing key block
    int key = dirval * FileContentStore.FILES_PER_DIRECTORY;
    // calculating next key block by switching to new directory
    key +=  FileContentStore.FILES_PER_DIRECTORY;

    System.out.println("Allocating key block " + key + " in " + cstore);
    allocateBlock(cstore, key);
  }

  private static void allocateBlock(File cstore, int new_key) throws ContentStoreException {
    boolean exception_thrown = false;
    File key_file = new File(cstore, "keyfile.txt");
    RandomAccessFile out = null;
    try {
      out = new RandomAccessFile(key_file, "rw");

      for (int i=0; i < FileContentStore.MAX_SPINS; i++) {
	// acquire exclusive lock
	FileLock l = out.getChannel().tryLock();
	
	if (l == null) {
	  // wait a little before trying again
	  try {
	    Thread.sleep(FileContentStore.SPIN_TIMEOUT);
	  } catch (InterruptedException e) {
	  }
	  continue;
	  
	} else {
	  
	  // truncate key file and write out new key
	  out.setLength(0);
	  out.writeUTF(Integer.toString(new_key));
	  return;
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
