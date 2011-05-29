
package net.ontopia.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    StreamUtils.transfer(src, out);
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

    StreamUtils.transfer(new EncryptedInputStream(in), out);
    
  }

}
