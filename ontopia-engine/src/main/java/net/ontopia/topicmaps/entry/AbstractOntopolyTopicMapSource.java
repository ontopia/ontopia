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

package net.ontopia.topicmaps.entry;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

import net.ontopia.utils.OntopiaRuntimeException;
import net.ontopia.utils.URIUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.impl.basic.InMemoryTopicMapStore;

/**
 * INTERNAL: Common abstract superclass for sources that support what
 * Ontopoly needs, which is full-text indexing and creation of new
 * topic maps.
 */
public abstract class AbstractOntopolyTopicMapSource
  extends AbstractPathTopicMapSource {

  protected boolean supportsCreate;
  protected boolean supportsDelete;
  
  protected boolean maintainFulltextIndexes;
  protected String indexDirectory;
  protected boolean alwaysReindexOnLoad = true;

  public AbstractOntopolyTopicMapSource() {
  }

  public AbstractOntopolyTopicMapSource(String path, String suffix) {
    this(path, suffix, null);
  }

  public AbstractOntopolyTopicMapSource(String path, FileFilter filter) {
    this(path, filter, null);
  }

  public AbstractOntopolyTopicMapSource(String path, String suffix,
                                        LocatorIF base_address) {
    super(path, suffix, base_address);
  }

  public AbstractOntopolyTopicMapSource(String path, FileFilter filter,
                                        LocatorIF base_address) {
    super(path, filter, base_address);
  }

  public boolean getMaintainFulltextIndexes() {
    return maintainFulltextIndexes; 
  }
  
  public void setMaintainFulltextIndexes(boolean maintainFulltextIndexes) {
    this.maintainFulltextIndexes = maintainFulltextIndexes;
  }

  public String getIndexDirectory() {
    return indexDirectory;
  }

  public void setIndexDirectory(String indexDirectory) {
    this.indexDirectory = indexDirectory;
  } 
  
  public boolean getAlwaysReindexOnLoad() {
    return this.alwaysReindexOnLoad;
  }

  public void setAlwaysReindexOnLoad(boolean alwaysReindexOnLoad) {
    this.alwaysReindexOnLoad = alwaysReindexOnLoad;
  }

  @Override
  public boolean supportsCreate() {
    return getSupportsCreate();
  }

  public boolean getSupportsCreate() {
    return supportsCreate;
  }
  
  public void setSupportsCreate(boolean supportsCreate) {
    this.supportsCreate = supportsCreate;
  }

  @Override
  public boolean supportsDelete() {
    return getSupportsDelete();
  }

  public boolean getSupportsDelete() {
    return supportsDelete;
  }
  
  public void setSupportsDelete(boolean supportsDelete) {
    this.supportsDelete = supportsDelete;
  }
  
  @Override
  public synchronized TopicMapReferenceIF createTopicMap(String name,
                                                         String baseAddress) {
    if (!supportsCreate()) {
      throw new UnsupportedOperationException("This source does not support creating new topic maps.");
    }
    if (path == null) {
      throw new OntopiaRuntimeException("Cannot create reference as source does not have a path specified.");
    }
    // make sure references map has been initialized
    getReferences();
    
    // construct reference properties
    String id = createReferenceId(name);
    File path = new File(this.path);
    File file = new File(path, id);
    URL url = URIUtils.toURL(file);

    // create new store    
    InMemoryTopicMapStore store = new InMemoryTopicMapStore();

    // persist topic map
    try {
      TopicMapWriterIF writer = getWriter(file);
      writer.write(store.getTopicMap());
    } catch (IOException e) {
      throw new OntopiaRuntimeException(e);
    } finally {
      if (store.isOpen()) {
        store.close();
      }
    }

    // create reference instance
    TopicMapReferenceIF ref = createReference(url, id, name, base_address);
    // put reference on reference map
    this.refmap.put(id, ref); // FIXME: what's this?
    
    return ref;
  }

  @Override
  public abstract TopicMapReferenceIF createReference(URL url, String id,
                                                      String title,
                                                      LocatorIF base_address);  
  /**
   * INTERNAL: Used by createTopicMap to serialize the new topic map.
   */
  protected abstract TopicMapWriterIF getWriter(File file)
    throws IOException;

  /**
   * INTERNAL: Creates well-formed reference ID from user-provided names.
   */
  private String createReferenceId(String name) {
    if (name == null || name.trim().equals("")) {
      name = "topicmap";
    } else {
      // lower casing (bug #1659) and replace all non-ascii characters
      char[] chars = name.toLowerCase().toCharArray();
      for (int i = 0; i < chars.length; i++) {
        char c = chars[i];
        if (!((c >= 'A' && c <= 'Z') ||
              (c >= 'a' && c <= 'z') ||
              (c >= '0' && c <= '9') ||
               c == '-')) {
          chars[i] = '_';
        }
      }
      name = new String(chars);
    }
    
    // avoid reference id collisions
    int cnt = 1;
    String id = name;
    if (!name.toLowerCase().endsWith(suffix)) {
      id += suffix;
    }
    
    while (refmap.containsKey(id)) {
      id = name + '-' + (cnt++) + suffix;
    }

    return id;
  } 
}
