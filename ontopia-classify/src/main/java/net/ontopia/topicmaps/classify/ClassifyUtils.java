
package net.ontopia.topicmaps.classify;

import java.io.*;
import java.util.Iterator;
import javax.servlet.http.*;
import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.StreamUtils;

import org.apache.commons.fileupload.*;

/**
 * INTERNAL: 
 */
public class ClassifyUtils {

  public static byte[] getContent(String file_or_url) {
    File file = new File(file_or_url);
    if (file.exists()) {
      try {
        return StreamUtils.read(new FileInputStream(file));
      } catch (IOException e) {
        // ignore
      }
    }
    // try file_or_url as URL
    return null;
  }

  public static ClassifiableContentIF getClassifiableContent(String file_or_url) {
    byte[] content = getContent(file_or_url);
    if (content != null) {
      ClassifiableContent cc = new ClassifiableContent();
      cc.setIdentifier(file_or_url);
      cc.setContent(content);
      return cc;
    }
    return null;
  }
  
  public static ClassifiableContentIF getFileUploadContent(HttpServletRequest request) {
    // Handle file upload
    String contentType = request.getHeader("content-type");
    // out.write("CT: " + contentType + " " + tm + " " + id);
    if (contentType != null && contentType.startsWith("multipart/form-data")) {
      try {
        FileUpload upload = new FileUpload(new DefaultFileItemFactory());
        Iterator iter = upload.parseRequest(request).iterator();
        if (iter.hasNext())  {
          FileItem item = (FileItem) iter.next();
          if (item.getSize() > 0) {
            // ISSUE: could make use of content type if known
            byte[] content = item.get();
            ClassifiableContent cc = new ClassifiableContent();
            String name = item.getName();
            if (name != null)
              cc.setIdentifier("fileupload:name:" + name);
            else
              cc.setIdentifier("fileupload:field:" + item.getFieldName());              
            cc.setContent(content);
            return cc;
          }      
        }
      } catch (Exception e) {
        throw new OntopiaRuntimeException(e);
      }
    }
    return null;
  }
  
}
