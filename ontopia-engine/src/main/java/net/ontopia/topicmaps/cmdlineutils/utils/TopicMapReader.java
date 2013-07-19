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

package net.ontopia.topicmaps.cmdlineutils.utils;

import java.io.IOException;
import java.net.MalformedURLException;

import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.xml.XTMTopicMapReader;
import net.ontopia.topicmaps.utils.ltm.LTMTopicMapReader;
import net.ontopia.utils.OntopiaRuntimeException;

public class TopicMapReader {

  static public TopicMapIF getTopicMap(String infile) 
    throws OntopiaRuntimeException, IOException, MalformedURLException {
    if (infile.endsWith(".xtm")) return new XTMTopicMapReader(infile).read();
    if (infile.endsWith(".ltm")) return new LTMTopicMapReader(infile).read();
    throw new OntopiaRuntimeException("Error with infile: suffix not supported");
  }

}





