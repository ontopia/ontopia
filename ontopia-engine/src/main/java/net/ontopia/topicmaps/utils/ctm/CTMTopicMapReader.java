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

package net.ontopia.topicmaps.utils.ctm;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicMapStoreFactoryIF;
import net.ontopia.topicmaps.core.TopicMapStoreIF;
import net.ontopia.topicmaps.impl.utils.AbstractTopicMapReader;
import net.ontopia.topicmaps.utils.ClassInstanceUtils;
import net.ontopia.topicmaps.utils.ltm.AntlrWrapException;

/**
 * PUBLIC: This TopicMapReader can read topic maps from the ISO-standard
 * CTM syntax. It implements the 2010-03-31 draft.
 * @since 4.0.5
 */
public class CTMTopicMapReader extends AbstractTopicMapReader {

  /**
   * PUBLIC: Creates a topic map reader bound to the URL given in the
   * arguments.
   * @param url The URL of the LTM file.
   */
  public CTMTopicMapReader(URL url) throws MalformedURLException {
    super(url);
  }

  public CTMTopicMapReader(URL url, LocatorIF base_address) {
    super(url, base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the reader given in
   * the arguments.
   * @param reader The reader from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.
   */
  public CTMTopicMapReader(Reader reader, LocatorIF base_address) {
    super(reader, base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the input stream
   * given in the arguments.
   * @param stream The input stream from which the topic map is to be read.
   * @param base_address The base address to be used for resolving
   * relative references.  */
  public CTMTopicMapReader(InputStream stream, LocatorIF base_address) {
    super(stream, base_address);
  }

  /**
   * PUBLIC: Creates a topic map reader bound to the file given in the
   * arguments.
   * @param file The file object from which to read the topic map.
   */
  public CTMTopicMapReader(File file) throws IOException {
    super(file);
  }

  // ==== READER IMPLEMENTATION ====

  @Override
  protected TopicMapIF read(TopicMapStoreFactoryIF store_factory)
      throws IOException {
    TopicMapStoreIF store = store_factory.createStore();
    TopicMapIF topicmap = store.getTopicMap();

    // Set base address on in-memory store
    if ((store instanceof net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore) &&
        store.getBaseAddress() == null) {
      ((net.ontopia.topicmaps.impl.utils.AbstractTopicMapStore)store)
          .setBaseAddress(getBaseAddress());
    }

    // Parse!
    try (Reader reader = makeReader((String) null, new CTMEncodingSniffer())) {
      CTMLexer lexer = new CTMLexer(reader);
      lexer.setDocuri(getBaseAddress().getAddress());
      CTMParser parser = new CTMParser(lexer);
      parser.setBase(getBaseAddress());
      parser.setTopicMap(topicmap, null);
      parser.init();
      parser.topicmap();
      reader.close();
    } catch (AntlrWrapException ex) {
      throw (IOException) ex.getException();
    } catch (RecognitionException ex) {
      throw new IOException("Lexical error at " + getBaseAddress().getAddress()
          + ":" + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    } catch (TokenStreamRecognitionException e) {
      RecognitionException ex = e.recog;
      throw new IOException("Lexical error at " + getBaseAddress().getAddress()
          + ":" + ex.line + ":" + ex.column + ": "+ ex.getMessage());
    } catch (TokenStreamIOException ex) {
      throw ex.io;
    } catch (TokenStreamException ex) {
      throw new IOException("Lexical error: " + ex.getMessage());
    } catch (java.io.CharConversionException e) {
      throw new IOException("Problem decoding character encoding."
                            + "Did you declare the right encoding?");
    }

    ClassInstanceUtils.resolveAssociations2(topicmap);
    return topicmap;
  }

  // ===== INTERNAL METHODS

  protected Reader makeReader(InputStream stream, String encoding)
    throws IOException {
    // FIXME: fill in auto-detection of encoding
    if (encoding == null) {
      encoding = "utf-8";
    }
    return new InputStreamReader(stream, encoding);
  }

}
