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

import java.io.File;
import java.util.Set;

import javax.swing.filechooser.FileFilter;

/**
 * INTERNAL:
 * Description: a simple file filter
 */

public class SimpleFileFilter extends FileFilter {

  private Set extensions;
  private String description;

  public SimpleFileFilter(String description) {

    this.description = description;
    this.extensions = new CompactHashSet();
  }

  public SimpleFileFilter(String description, String extension) {

    this.description = description;
    this.extensions = new CompactHashSet();
    addExtension(extension);
  }

  public SimpleFileFilter(String description, String ext1, String ext2) {

    this.description = description;
    this.extensions = new CompactHashSet();
    addExtension(ext1);
    addExtension(ext2);
  }

  public void addExtension(String extension) {

    extensions.add(extension.toLowerCase());
  }

  @Override
  public String getDescription() {

    return description;
  }

  @Override
  public boolean accept(File f) {

    if (f.isDirectory()) return true;

    String name = f.getName();
    int pos = name.lastIndexOf('.');
    return pos != -1
        && extensions.contains(name.substring(pos + 1).toLowerCase());
  }
}
