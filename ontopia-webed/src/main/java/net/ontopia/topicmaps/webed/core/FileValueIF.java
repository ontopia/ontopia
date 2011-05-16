
// $Id: FileValueIF.java,v 1.2 2003/12/22 21:12:25 larsga Exp $

package net.ontopia.topicmaps.webed.core;

import java.io.InputStream;

/**
 * PUBLIC: Represents files passed as parameters to forms.
 *
 * @since 2.0
 */
public interface FileValueIF {

  /**
   * PUBLIC: Returns the name of the file as given in the request.
   */
  public String getFileName();

  /**
   * PUBLIC: Returns an input stream from which the file can be
   * retrieved.
   */  
  public InputStream getContents() throws java.io.IOException;

  /**
   * PUBLIC: Returns the length of the file in bytes.
   */
  public long getLength();

  /**
   * PUBLIC: Returns the content type given for this file in the
   * request.  May be null.
   */  
  public String getContentType();
  
}
