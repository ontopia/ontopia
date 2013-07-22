/*
 * #!
 * Ontopia Webed
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

package net.ontopia.topicmaps.webed.impl.actions.occurrence;

import java.net.MalformedURLException;
import java.util.Iterator;

import net.ontopia.infoset.content.ContentStoreException;
import net.ontopia.infoset.content.ContentStoreIF;
import net.ontopia.infoset.content.ContentStoreUtils;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.impl.basic.URILocator;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.query.core.InvalidQueryException;
import net.ontopia.topicmaps.query.core.QueryProcessorIF;
import net.ontopia.topicmaps.query.core.QueryResultIF;
import net.ontopia.topicmaps.query.utils.QueryUtils;
import net.ontopia.topicmaps.webed.core.ActionIF;
import net.ontopia.topicmaps.webed.core.ActionParametersIF;
import net.ontopia.topicmaps.webed.core.ActionResponseIF;
import net.ontopia.topicmaps.webed.core.ActionRuntimeException;
import net.ontopia.topicmaps.webed.core.FileValueIF;

/**
 * INTERNAL: Action for uploading a file as an external occurrence.
 *
 * @since 2.0.3
 */
public class UploadFile implements ActionIF {

  public void perform(ActionParametersIF params, ActionResponseIF response) {

    try {
      // initialize
      OccurrenceIF occ = (OccurrenceIF) params.get(0);
      TopicMapIF tm = occ.getTopicMap();
      ContentStoreIF store = ContentStoreUtils.getContentStore(tm, null);
    
      // write content into store
      FileValueIF file = params.getFileValue();
      int key = store.add(file.getContents(), (int) file.getLength());

      // check if there already is a file there; if so, delete
      if (occ.getLocator() != null) {
        String uri = occ.getLocator().getAddress();
        if (uri.startsWith("x-ontopia:cms:") && noOtherReference(occ))
          store.remove(Integer.parseInt(uri.substring(14)));
      }
    
      // create new locator for content, put into occurrence
      LocatorIF loc = new URILocator("x-ontopia:cms:" + key);
      occ.setLocator(loc);
    
      // store filename on reifying topic
      TopicIF reifier = occ.getReifier();
      TopicIF filename = getTopicById(tm, "filename");

      OccurrenceIF occurrence = null;
      Iterator it = reifier.getOccurrences().iterator();
      while (it.hasNext()) {
        occ = (OccurrenceIF) it.next();
        if (occ.getType() != null && occ.getType().equals(filename)) {
          occurrence = occ;
          break;
        }
      }

      if (occurrence == null) { // no previously recorded filename
        TopicMapBuilderIF builder = tm.getBuilder();
        occurrence = builder.makeOccurrence(reifier, filename, file.getFileName());
      } else {
				occurrence.setValue(file.getFileName());
			}
    } catch (ContentStoreException e) {
      throw new ActionRuntimeException(e);
    } catch (MalformedURLException e) {
      throw new ActionRuntimeException(e);
    } catch (java.io.IOException e) {
      throw new ActionRuntimeException("Error when saving file to content store", e);
    }
  }

  // --- Internal methods

  private TopicIF getTopicById(TopicMapIF topicmap, String id) {
    LocatorIF base = topicmap.getStore().getBaseAddress();
    LocatorIF srcloc = base.resolveAbsolute('#' + id);
    return (TopicIF) topicmap.getObjectByItemIdentifier(srcloc);
  }

  private boolean noOtherReference(OccurrenceIF occurrence) {
    try {
      QueryProcessorIF processor =
        QueryUtils.getQueryProcessor(occurrence.getTopicMap());
      QueryResultIF result = processor.execute(
        "select $OBJ from " +
        "resource($OBJ, \"" + occurrence.getLocator().getAddress() + "\"), " +
        "$OBJ /= @" + occurrence.getObjectId() + "?");
      boolean other = result.next();
      result.close();
      return other;
    } catch (InvalidQueryException e) {
      throw new ActionRuntimeException(e);
    }
  }
  
}
