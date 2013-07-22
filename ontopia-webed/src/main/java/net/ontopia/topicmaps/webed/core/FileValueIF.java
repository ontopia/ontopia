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
