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
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * PUBLIC: A topic map writer that can write topic maps out into the
 * interchange syntax defined by the 2010-03-02 draft of XTM 2.1,
 * as published at http://www.itscj.ipsj.or.jp/sc34/open/1378.htm
 *
 * @since 5.1.0
 */
public final class XTM21TopicMapWriter extends AbstractXTM2TopicMapWriter {

  public XTM21TopicMapWriter(File file) throws IOException {
    super(file);
  }

  public XTM21TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
  }

  public XTM21TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    super(stream, encoding);
  }

  /**
   * PUBLIC: Creates a writer which writes to the given writer and
   * claims that the file is in the given encoding. <b>Warning:</b>
   * we do <em>not</em> recommend using this method, as there is
   * no guarantee that the declared encoding and the real encoding
   * will
   */
  public XTM21TopicMapWriter(Writer writer, String encoding) throws IOException {
    super(writer, encoding);
  }

  @Override
  protected XTMVersion getVersion() {
    return XTMVersion.XTM_2_1;
  }

}
