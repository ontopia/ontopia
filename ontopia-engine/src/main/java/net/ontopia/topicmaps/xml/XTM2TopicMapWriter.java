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
 * interchange syntax defined by XTM 2.0.
 *
 * @since 4.0.0
 */
public final class XTM2TopicMapWriter extends AbstractXTM2TopicMapWriter {

  public XTM2TopicMapWriter(File file) throws IOException {
    super(file);
  }

  public XTM2TopicMapWriter(File file, String encoding) throws IOException {
    super(file, encoding);
  }

  public XTM2TopicMapWriter(OutputStream stream, String encoding)
    throws IOException, UnsupportedEncodingException {
    super(stream, encoding);
  }

  public XTM2TopicMapWriter(Writer writer, String encoding) throws IOException {
    super(writer, encoding);
  }

  @Override
  protected XTMVersion getVersion() {
    return XTMVersion.XTM_2_0;
  }

}
 