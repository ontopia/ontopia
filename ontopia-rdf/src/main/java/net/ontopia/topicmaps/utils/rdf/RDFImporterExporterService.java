/*
 * #!
 * Ontopia RDF
 * #-
 * Copyright (C) 2001 - 2014 The Ontopia Project
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
package net.ontopia.topicmaps.utils.rdf;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.TopicMapReaderIF;
import net.ontopia.topicmaps.core.TopicMapWriterIF;
import net.ontopia.topicmaps.entry.AbstractURLTopicMapReference;
import net.ontopia.topicmaps.utils.ImportExportServiceIF;

/**
 * ImportExportServiceIF service providing RDF import and export functionality. 
 * @since 5.4.0
 */
public class RDFImporterExporterService implements ImportExportServiceIF {

  @Override
  public boolean canRead(URL resource) {
    String resourceString = resource.toString().toLowerCase();
    return resourceString.endsWith(".rdf")
            || resourceString.endsWith(".n3")
            || resourceString.endsWith(".nt");
  }

  @Override
  public boolean canWrite(URL resource) {
    return canRead(resource);
  }

  @Override
  public TopicMapWriterIF getWriter(OutputStream stream) throws IOException {
    return new RDFTopicMapWriter(stream);
  }

  @Override
  public TopicMapReaderIF getReader(URL resource) {
    return new RDFTopicMapReader(resource, getSyntax(resource));
  }

  private String getSyntax(URL resource) {
    String resourceString = resource.toString().toLowerCase();
    if (resourceString.endsWith(".rdf")) {
      return "RDF/XML";
    } else if (resourceString.endsWith(".n3")) {
      return "N3";
    } else if (resourceString.endsWith(".nt")) {
      return "N-TRIPLE";
    }
    return null;
  }

  @Override
  public AbstractURLTopicMapReference createReference(URL url, String refid, String title, LocatorIF base_address) {
    return new RDFTopicMapReference(url, refid, title, base_address, getSyntax(url));
  }
}
