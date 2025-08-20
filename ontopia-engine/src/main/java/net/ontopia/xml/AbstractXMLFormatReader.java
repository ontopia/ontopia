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

package net.ontopia.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.utils.URIUtils;
import org.xml.sax.InputSource;

/**
 * INTERNAL: A common base class for Reader implementations that can
 * read XML-based syntaxes.
 * @since 1.2
 */
public abstract class AbstractXMLFormatReader {
  protected InputSource source;
  protected LocatorIF base_address;

  public AbstractXMLFormatReader() {
  }

  public AbstractXMLFormatReader(InputSource source, LocatorIF base_address) {
    this.source = source;
    this.base_address = base_address;
  }

  public AbstractXMLFormatReader(URL url) {
    this(new InputSource(url.toString()), new URILocator(url));
  }
  
  public AbstractXMLFormatReader(URL url, LocatorIF base_address) {
    this(new InputSource(url.toString()), base_address);
  }
  
  public AbstractXMLFormatReader(Reader reader, LocatorIF base_address) {
    this(new InputSource(reader), base_address);
  }
  
  public AbstractXMLFormatReader(InputStream stream, LocatorIF base_address) {
    this(new InputSource(stream), base_address);
  }
  
  public AbstractXMLFormatReader(File file) {
    this(URIUtils.toURL(file));
  }
  
  /**
   * INTERNAL: Gets the SAX input source used by the reader.
   */
  public InputSource getInputSource() {
    return source;
  }

  /**
   * INTERNAL: Sets the SAX input source used by the reader.
   */
  public void setInputSource(InputSource source) {
    this.source = source;
  }
  
  /**
   * INTERNAL: Gets the top level base address of the input source.
   */
  public LocatorIF getBaseAddress() {
    return base_address;
  }

  /**
   * INTERNAL: Sets the top level base address of the input source.</p>
   *
   * The top level base address is used to resolve relative addresses
   * during input source processing. The top level base address can be
   * overriden by xml:base constructs in the input source. This
   * property need not be set if the input source specifies the base
   * address.</p>
   */
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
  }
}
