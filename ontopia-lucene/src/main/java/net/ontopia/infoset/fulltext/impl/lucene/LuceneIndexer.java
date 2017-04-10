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

package net.ontopia.infoset.fulltext.impl.lucene;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Iterator;
import net.ontopia.infoset.fulltext.core.DocumentIF;
import net.ontopia.infoset.fulltext.core.FieldIF;
import net.ontopia.infoset.fulltext.core.IndexerIF;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: The Lucene indexer implementation. This indexer uses the
 * Lucene search engine to index documents.<p>
 */
public class LuceneIndexer implements IndexerIF {

  // Define a logging category.
  private static Logger log = LoggerFactory.getLogger(LuceneIndexer.class.getName());

  protected IndexWriter writer;

  /**
   * INTERNAL: Creates an indexer instance that will store its index in
   * the given lucene directory and use the specified token stream
   * analyzer.<p>
   */
  public LuceneIndexer(IndexWriter writer) {
    this.writer = writer;
  }

  /**
   * INTERNAL: Returns the number of documents stored in the index.
   */
  public synchronized int getDocs() throws IOException {
    return writer.numDocs();
  }
  
  public synchronized void index(DocumentIF document) throws IOException {
    writer.addDocument(getDocument(document));
  }
  
  public synchronized void delete(String field, String value) throws IOException {
    writer.deleteDocuments(new Term(field, value));
  }

  public synchronized void flush() throws IOException {
  }
  
  public synchronized void delete() throws IOException {
  }

  public synchronized void close() throws IOException {
  }
  
  protected Document getDocument(DocumentIF document) throws IOException {
    Document lucene_document = new Document();
    Iterator<FieldIF> iter = document.getFields().iterator();
    while (iter.hasNext()) {      
      FieldIF field = iter.next();
      lucene_document.add(getField(field));
    }
    return lucene_document;
  }

  protected Field getField(FieldIF field) throws IOException {
    Field lucene_field;
    if (field.getReader() != null) {
      if (!field.isStored() && field.isIndexed() && field.isTokenized())
        lucene_field = new Field(field.getName(), field.getReader()); // Reader based field
      else {
        lucene_field = new Field(field.getName(), getStringValue(field.getReader()),
            getStoreSetting(field), getIndexSetting(field));
      }
    } else {
      lucene_field = new Field(field.getName(), field.getValue(), getStoreSetting(field), getIndexSetting(field));
    }
    return lucene_field;
  }
  
  protected Field.Store getStoreSetting(FieldIF field) {
    if (field.isStored()) {
      return Field.Store.YES;
    } else {
      return Field.Store.NO;
    }
  }
  
  protected Field.Index getIndexSetting(FieldIF field) {
    if (field.isIndexed()) {
      if (field.isTokenized()) {
        return Field.Index.ANALYZED;
      } else {
        return Field.Index.NOT_ANALYZED;
      }
    } else {
      return Field.Index.NO;
    }
  }

  protected String getStringValue(Reader reader) throws IOException {
    // Read the reader contents into a string
    StringWriter swriter = new StringWriter();
    int c;
    while ((c = reader.read()) != -1)
      swriter.write(c);
    return swriter.getBuffer().toString();
  }
}