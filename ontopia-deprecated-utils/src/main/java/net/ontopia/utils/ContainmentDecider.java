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

package net.ontopia.utils;

import java.util.Collection;
import java.util.HashSet;

/**
 * INTERNAL: Decider that returns true if the object is contained in
 * the referenced collection.
 *
 * @since 4.0
 */

@Deprecated
public class ContainmentDecider<T> implements DeciderIF<T> {

    private Collection<T> objects = new HashSet<T>();
    
    public ContainmentDecider(Collection<T> objects) {
      this.objects = objects;
    }
    
    @Override
    public boolean ok(T o) {
      return objects.contains(o);
    }

}




