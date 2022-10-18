/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import net.ontopia.topicmaps.webed.core.FileValueIF;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: Utility class for providing helper methods handling
 * servlet request parameters (mainly for string manipulation
 * purposes).
 */
public final class ReqParamUtils {

  // initialization of logging facility
  private static Logger log = LoggerFactory
    .getLogger(ReqParamUtils.class.getName());
 
  /**
   * INTERNAL: Generate string which can be used to append to an
   * URL. Use all request parameter (that are key, value) pairs from
   * the provided <code>extraReqParams</code> Map object.
   *
   * @param charenc the character encoding to use in the URL
   */
  public static final String params2URLQuery(Map extraReqParams,
                                             Parameters reqparams,
                                             String charenc)
    throws IOException {
    StringBuilder urlQuery = new StringBuilder(48);
    boolean seenFirstPair = false;
    Iterator it = extraReqParams.keySet().iterator();
    while (it.hasNext()) {
      // first try to lookup if fixed value otherwise get it from request
      String key = (String) it.next();
      String val = (String) extraReqParams.get(key);
      if (val == null && reqparams != null)
          val = reqparams.get(key);

      // URL encode the request parameter value
      if (val != null) {
        if (seenFirstPair)
          urlQuery.append("&");
        else
          seenFirstPair = true;
        if (charenc == null) {
          charenc = "utf-8";
        }
        urlQuery.append(key).append("=").append(URLEncoder.encode(val, charenc));
      }
    }
    return urlQuery.toString();
  }

  /**
   * INTERNAL: Parses the query part of a URL to extract the parameter
   * names and values.
   */
  public static Map parseURLQuery(String url) {
    Map params = new HashMap();
    
    // start at beginning of query part, before first param name
    int ix = 0;

    while (ix < url.length()) {
      int start = ix;

      // scan for end of param name (or url)
      for (; ix < url.length() && url.charAt(ix) != '='; ix++)
        ;

      // does url end at end of param name?
      int equalsAt = ix;
      if (url.charAt(ix) != '=')
        break; // FIXME: ought to report an error here...

      // scan through param value
      for (; ix < url.length() && url.charAt(ix) != '&'; ix++)
        ;

      params.put(url.substring(start, equalsAt),
                 url.substring(equalsAt + 1, ix));

      ix++;
    }

    return params;
  }

  /**
   * INTERNAL: Builds the Parameters object from an HttpServletRequest
   * object.
   * @since 2.0
   */
  public static Parameters decodeParameters(HttpServletRequest request,
                                            String charenc)
    throws ServletException, IOException {

    String ctype = request.getHeader("content-type");
    log.debug("Content-type: " + ctype);
    Parameters params = new Parameters();      
    
    if (ctype != null && ctype.startsWith("multipart/form-data")) {
      // special file upload request, so use FileUpload to decode
      log.debug("Decoding with FileUpload; charenc="+ charenc);
      try {
        FileUpload upload = new FileUpload(new DefaultFileItemFactory());
        Iterator iterator = upload.parseRequest(request).iterator();
        while (iterator.hasNext()) {
          FileItem item = (FileItem) iterator.next();
          log.debug("Reading: " + item);
          if (item.isFormField()) {
            if (charenc != null)
              params.addParameter(item.getFieldName(), item.getString(charenc));
            else
              params.addParameter(item.getFieldName(), item.getString());
          } else
            params.addParameter(item.getFieldName(), new FileParameter(item));
        }
      } catch (FileUploadException e) {
        throw new ServletException(e);
      } 
      
    } else {
      // ordinary web request, so retrieve info and stuff into Parameters object
      log.debug("Normal parameter decode, charenc=" + charenc);
      if (charenc != null)
        request.setCharacterEncoding(charenc);
      Enumeration enumeration = request.getParameterNames();
      while (enumeration.hasMoreElements()) {
        String param = (String) enumeration.nextElement();
        params.addParameter(param, request.getParameterValues(param));
      }
    }

    return params;
  }  

  // --- Internal helper class for wrapping FileItems

  /**
   * INTERNAL: We use this to avoid having other parts of the API
   * depend on the FileUpload API.
   */
  static class FileParameter implements FileValueIF {
    private FileItem file;

    public FileParameter(FileItem file) {
      this.file = file;
    }
    
    @Override
    public String getFileName() {
      return file.getName();
    }

    @Override
    public InputStream getContents() throws IOException {
      return file.getInputStream();
    }

    @Override
    public long getLength() {
      return file.getSize();
    }

    @Override
    public String getContentType() {
      return file.getContentType();
    }    
  }
}
