/*
 * #!
 * Ontopoly Editor
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
package ontopoly.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import net.ontopia.topicmaps.core.OccurrenceIF;
import org.apache.commons.io.input.ReaderInputStream;
import ontopoly.models.TMObjectModel;
import org.apache.commons.codec.binary.Base64InputStream;

import org.apache.wicket.markup.html.WebResource;
import org.apache.wicket.util.resource.AbstractResourceStream;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;

public class OccurrenceWebResource extends WebResource {

  protected TMObjectModel omodel;
  
  public OccurrenceWebResource() {
  }
  
  public OccurrenceWebResource(TMObjectModel omodel) {
    this.omodel = omodel;
  }

  protected Reader getReader() {
    OccurrenceIF occ = (OccurrenceIF)omodel.getObject();
    return (occ == null ? null : occ.getReader());
  }
  
  @Override
  public IResourceStream getResourceStream() {
    Reader reader = getReader();
    return (reader == null ? null : new Base64EncodedResourceStream(reader));
  }

  public static final IResourceStream getResourceStream(OccurrenceIF occ)  {
    if (occ == null) {
      return null;
    }
    Reader reader = occ.getReader();
    return (reader == null ? null : new Base64EncodedResourceStream(reader));
  }
  
  public static class Base64EncodedResourceStream extends AbstractResourceStream {

    protected Reader reader;
    
    Base64EncodedResourceStream(Reader reader) {
      this.reader = reader;
    }
    
    @Override
    public InputStream getInputStream() throws ResourceStreamNotFoundException {
        return new Base64InputStream(new ReaderInputStream(reader, "utf-8"), false);
    }

    @Override
    public void close() throws IOException {
      reader.close();
    }
    
  }
}
