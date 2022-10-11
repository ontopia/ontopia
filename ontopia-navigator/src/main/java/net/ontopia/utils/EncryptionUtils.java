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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: Utilities for encrypting files. Replaces the old
 * EncryptionWriter.
 * @since 1.3.3
 */
public class EncryptionUtils {

  /**
   * INTERNAL: Reads the file into memory, encrypting it in the
   * process, then writes the encrypted data back out to the file.
   */
  public static void encrypt(File file) throws IOException {
    FileInputStream in = new FileInputStream(file);
    ByteArrayOutputStream tmpout = new ByteArrayOutputStream();
    encrypt(in, tmpout);
    in.close();

    FileOutputStream out = new FileOutputStream(file);
    ByteArrayInputStream src = new ByteArrayInputStream(tmpout.toByteArray());
    IOUtils.copy(src, out);
    out.close();
  }

  /**
   * INTERNAL: Reads in the infile and writes the encrypted result
   * into the outfile.
   */
  public static void encrypt(File infile, File outfile) throws IOException {
    FileInputStream in = new FileInputStream(infile);
    FileOutputStream out = new FileOutputStream(outfile);
    encrypt(in, out);
    in.close();
    out.close();
  }

  /**
   * INTERNAL: Reads all the data in the InputStream, encrypts it, and
   * writes it to the OutputStream.
   */
  public static void encrypt(InputStream in, OutputStream out)
    throws IOException {

    IOUtils.copy(new EncryptedInputStream(in), out);
    
  }

}
