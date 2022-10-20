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

import java.util.HashMap;
import java.util.Map;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * INTERNAL: SAX entity resolver that makes sure that doctypes
 * referencing the XTM 1.0 DTD is ignored.<p>
 *
 * Public id mappings take precedence over system id mappings.<p>
 */

public class ConfigurableEntityResolver implements EntityResolver {

  protected Map public_ids;
  protected Map system_ids;

  public ConfigurableEntityResolver() {
    public_ids = new HashMap();
    system_ids = new HashMap();
  }

  /**
   * INTERNAL: Registers the input source factory with the given public id.
   */
  public void addPublicIdSource(String public_id, InputSourceFactoryIF is_factory) {
    public_ids.put(public_id, is_factory);
  }

  /**
   * INTERNAL: Unregisters the input source factory registered with the
   * given public id.
   */
  public void removePublicIdSource(String public_id) {
    public_ids.remove(public_id);
  }

  /**
   * INTERNAL: Registers the input source factory with the given system id.
   */
  public void addSystemIdSource(String system_id, InputSourceFactoryIF is_factory) {
    public_ids.put(system_id, is_factory);
  }

  /**
   * INTERNAL: Unregisters the input source factory registered with the
   * given system id.
   */
  public void removeSystemIdSource(String system_id) {
    system_ids.remove(system_id);
  }
  
  @Override
  public InputSource resolveEntity (String public_id, String system_id) {

    // Check to see if a public id input source factory exist
    if (public_ids.containsKey(public_id)) {
      InputSourceFactoryIF is_factory = (InputSourceFactoryIF)public_ids.get(public_id);
      if (is_factory != null) {
        return is_factory.createInputSource();
      }
      return null;
    }
    // Check to see if a system id input source factory exist
    if (system_ids.containsKey(system_id)) {
	InputSourceFactoryIF is_factory = (InputSourceFactoryIF)system_ids.get(system_id);
	if (is_factory != null) {
    return is_factory.createInputSource();
  }      
	return null;
    }
    // Use the default
    return null;
  }
  
}
