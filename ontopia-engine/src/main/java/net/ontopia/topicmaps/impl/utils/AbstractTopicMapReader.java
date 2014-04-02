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

package net.ontopia.topicmaps.impl.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.net.URL;

import org.xml.sax.InputSource;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapImporterIF;
import net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory;
import net.ontopia.topicmaps.utils.SameStoreFactory;

/**
 * INTERNAL: Common abstract superclass for topic map readers.
 */
public abstract class AbstractTopicMapReader
  implements TopicMapReaderIF, TopicMapImporterIF {
  protected InputSource source;
  protected LocatorIF base_address;
  protected TopicMapStoreFactoryIF store_factory;

  /**
   * PUBLIC: Gets the SAX input source used by the reader.
   */
  public InputSource getInputSource() {
    return source;
  }

  /**
   * PUBLIC: Sets the SAX input source used by the reader.
   */
  public void setInputSource(InputSource source) {
    this.source = source;
  }
  
  /**
   * PUBLIC: Gets the top level base address of the input source.
   */
  public LocatorIF getBaseAddress() {
    return base_address;
  }

  /**
   * PUBLIC: Sets the top level base address of the input source.</p>
   *
   * The top level base address is used to resolve relative addresses
   * during input source processing. This property need not be set if
   * the input source specifies the base address.</p>   
   */
  public void setBaseAddress(LocatorIF base_address) {
    this.base_address = base_address;
  }

  /**
   * PUBLIC: Gets the store factory which will be used to create stores.
   */
  public TopicMapStoreFactoryIF getStoreFactory() {
    // Initialize default factory
    if (store_factory == null) {
      store_factory = new InMemoryStoreFactory();
    }
    return store_factory;
  }

  /**
   * PUBLIC: Sets the store factory which will be used to create stores.</p>
   *
   * <p>Default: {@link
   * net.ontopia.topicmaps.impl.basic.InMemoryStoreFactory}</p>
   *
   * @param store_factory The store factory to use. If the parameter
   * is null the default store factory will be used.
   */
  public void setStoreFactory(TopicMapStoreFactoryIF store_factory) {
    this.store_factory = store_factory;
  }

  // ==== READER IMPLEMENTATION ====
  
  public TopicMapIF read() throws IOException {
    return read(getStoreFactory());
  }

  public Collection<TopicMapIF> readAll() throws IOException {
    return readAll(getStoreFactory());
  }

  protected Collection<TopicMapIF> readAll(TopicMapStoreFactoryIF store_factory) 
      throws IOException {
    // we assume the data source must by necessity contain only a
    // single topic map; override if this assumption is wrong.

    return Collections.singleton(read(store_factory));
  }

  // the real implementation, specific to each syntax
  protected abstract TopicMapIF read(TopicMapStoreFactoryIF store_factory) 
    throws IOException;
  
  /**
   * Default implemenentation does not accept any additional properties
   * @param properties 
   */
  public void setAdditionalProperties(Map<String, Object> properties) {
    // no-op
  }

  // ==== IMPORTER IMPLEMENTATION ====

  public void importInto(TopicMapIF topicmap) throws IOException {
    // Check that store is ok
    TopicMapStoreIF store = topicmap.getStore();
    if (store == null)
      throw new IOException("Topic map not connected to a store.");

    // Use a store factory that always returns the same topic
    // map. This makes sure that all topic maps found inside the
    // source document will be imported into the document.
    
    // Read all topic maps from the source.
    readAll(new SameStoreFactory(store));
  }

  // ===== HELPER METHODS

  /**
   * INTERNAL: Creates a correctly configured Reader from a SAX
   * InputSource object.
   */   
  public static Reader makeReader(InputSource source, EncodingSnifferIF sniffer)
    throws IOException {
    Reader reader = source.getCharacterStream();
    if (reader == null) {
      if (source.getByteStream() != null) 
        reader = makeReader(source.getByteStream(), source.getEncoding(), sniffer);
      else {
        URL url = new URL(source.getSystemId());
        reader = makeReader(url.openStream(), null, sniffer);
      }
    }
    return reader;
  }
  
  public static Reader makeReader(InputStream stream, String encoding,
                                  EncodingSnifferIF sniffer) throws IOException {
    if (encoding == null) {
      // BEWARE: next statements means we make a reader from the pushbackstream!
      // this is crucial, since otherwise the first 50 bytes of the stream are lost
      stream = new PushbackInputStream(stream, 50);
      encoding = sniffer.guessEncoding((PushbackInputStream) stream);
    }
    return new InputStreamReader(stream, encoding);
  }
}
