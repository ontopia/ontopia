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

package net.ontopia.infoset.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.infoset.core.LocatorReaderFactoryIF;

/**
 * INTERNAL: Creates a Reader for a LocatorIF that contains a URL in
 * its address field.<p>
 *
 * Warning: At this point the reader uses the default charcter
 * encoding. In the future the correct encoding will be chosen based
 * on information from the actual URL connection.<p>
 */

public class URLLocatorReaderFactory implements LocatorReaderFactoryIF {

  public URLLocatorReaderFactory() {
  }  
  
  public Reader createReader(LocatorIF locator) throws IOException {
    URL url = new URL(locator.getAddress());
    return new InputStreamReader(url.openConnection().getInputStream());
  }
  
}





