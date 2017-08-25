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

/**
 * INTERNAL: Grabber that grabs a substring from the String object given
 * to it.
 */

public class SubstringGrabber implements GrabberIF<Object, String> {

  protected int begin_index;
  protected int end_index;
  
  public SubstringGrabber(int begin_index, int end_index) {
    this.begin_index = begin_index;
    this.end_index = end_index;
  }
  
  @Override
  public String grab(Object object) {
    if (object.toString().length() == 0) return "";                                      
    return object.toString().substring(begin_index, end_index);
  }
  
}




