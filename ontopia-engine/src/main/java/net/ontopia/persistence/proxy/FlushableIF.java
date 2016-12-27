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

package net.ontopia.persistence.proxy;
  
/**
 * INTERNAL: Interface implemented by data repository accessors that
 * needs to be informed when changes to the repository needs to be
 * performed.<p>
 *
 * This interface can thus be used to implement optimized data
 * repository access.<p>
 */

public interface FlushableIF {

  /**
   * INTERNAL: Tells the object to flush itself.
   */
  void flush() throws Exception;
  
}






