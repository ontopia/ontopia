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
import net.ontopia.topicmaps.utils.ImportExportServiceIF;
import net.ontopia.utils.ServiceUtils;
import org.junit.Assert;
import org.junit.Test;

public class RDImportExportFServiceTest {

  @Test
  public void testServiceLoaded() throws IOException {
    for (ImportExportServiceIF service : ServiceUtils.loadServices(ImportExportServiceIF.class)) {
      if (service.getClass().equals(RDFImporterExporterService.class)) {
        return;
      }
    }
    Assert.fail("RDFImporterExporterService was not found in the list of ImportExportServiceIF services");
  }

}
