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

package net.ontopia.topicmaps.xml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * PUBLIC: A topic map writer that can write topic maps out into the
 * interchange syntax defined by XTM 2.0 or 2.1.
 *
 * @since 5.1.0
 */
abstract class AbstractXTM2TopicMapWriter extends XTMTopicMapWriter {

  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param filename The name of the file to which the topic map is to
   * be written.
   */ 
  public AbstractXTM2TopicMapWriter(String filename) throws IOException {
    this(new File(filename), "utf-8");
  }
  
  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.  The topic map will be written out in the UTF-8
   * encoding.   
   * @param file The file object to which the topic map is to be written.
   */
  public AbstractXTM2TopicMapWriter(File file) throws IOException {
    this(file, "utf-8");
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the file given in the
   * arguments.   
   * @param file The file object to which the topic map is to be written.
   * @param encoding The character encoding to write the topic map in.
   */
  public AbstractXTM2TopicMapWriter(File file, String encoding)
    throws IOException {
    super(file, encoding);
    setVersion(getVersion());
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the output stream
   * given in the arguments.   
   * @param stream The output stream to which the topic map is to be
   * written.
   * @param encoding The character encoding to write the topic map in.
   * @exception UnsupportedEncodingException Thrown when the character
   * encoding is not supported by the Java environment.
   */
  public AbstractXTM2TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    this(new OutputStreamWriter(stream, encoding), encoding);
  }

  /**
   * PUBLIC: Creates a topic map writer bound to the Writer given in
   * the arguments; we do <em>not</em> recommend the use of this
   * method.
   *
   * @param writer The Writer to which the topic map is to be
   * written.
   * @param encoding The character encoding the Writer writes in.
   * Note that this <em>must</em> be set correctly, or the XML
   * document will not parse correctly.
   */
  public AbstractXTM2TopicMapWriter(Writer writer, String encoding)
    throws IOException {
    super(writer, encoding);
    setVersion(getVersion());
  }

  /**
   * Returns the XTM version of the derived class.
   */
  protected abstract XTMVersion getVersion();

}
