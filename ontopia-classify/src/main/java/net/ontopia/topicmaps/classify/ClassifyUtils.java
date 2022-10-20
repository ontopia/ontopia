/*
 * #!
 * Ontopia Classify
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

package net.ontopia.topicmaps.classify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import net.ontopia.utils.OntopiaRuntimeException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.io.IOUtils;

/**
 * INTERNAL: 
 */
public class ClassifyUtils {

  public static byte[] getContent(String file_or_url) {
    File file = new File(file_or_url);
    if (file.exists()) {
      try (InputStream in = new FileInputStream(file)){
        return IOUtils.toByteArray(in);
      } catch (IOException e) {
        // ignore
      }
    }
    // try file_or_url as URL
    return null;
  }

  public static ClassifiableContentIF getClassifiableContent(String file_or_url) {
    return getClassifiableContent(getContent(file_or_url), file_or_url);
  }

  public static ClassifiableContentIF getClassifiableContent(byte[] content) {
    return getClassifiableContent(content, "content-" + DigestUtils.md5Hex(content));
  }

  private static ClassifiableContentIF getClassifiableContent(byte[] content, String identifier) {
    if (content != null) {
      ClassifiableContent cc = new ClassifiableContent();
      cc.setIdentifier(identifier);
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
        for (FileItem item : upload.parseRequest(request)) {
          if (item.getSize() > 0) {
            // ISSUE: could make use of content type if known
            byte[] content = item.get();
            ClassifiableContent cc = new ClassifiableContent();
            String name = item.getName();
            if (name != null) {
              cc.setIdentifier("fileupload:name:" + name);
            } else {
              cc.setIdentifier("fileupload:field:" + item.getFieldName());
            }              
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
