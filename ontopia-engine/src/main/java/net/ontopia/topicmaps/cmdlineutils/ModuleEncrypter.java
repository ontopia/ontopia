
package net.ontopia.topicmaps.cmdlineutils;

import java.io.File;

import net.ontopia.utils.EncryptionUtils;

public class ModuleEncrypter {

  public static void main(String[] args) throws java.io.IOException {
    for (int ix = 0; ix < args.length; ix++) {
      File arg = new File(args[ix]);

      File[] files = null;
      if (arg.isDirectory())
        files = arg.listFiles();
      else {
        files = new File[1];
        files[0] = arg;
      }
        
      for (int i = 0; i < files.length; i++) {
        EncryptionUtils.encrypt(files[i]);
        System.out.println("encrypted module file: " + files[i]);
      }
    }
  }
}
