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

package net.ontopia.topicmaps.utils.rdf;

import net.ontopia.utils.OntopiaRuntimeException;

import com.hp.hpl.jena.rdfxml.xmlinput.ALiteral;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.shared.JenaException;

/**
 * INTERNAL: This class is used to wrap Jena Literal objects in the
 * ARP ALiteral interface so that they can be streamed through the ARP
 * StatementHandler interface without requiring new objects to be
 * created.
 */
public class ALiteralWrapper implements ALiteral {
  public Literal literal;

  private boolean tainted;

  @Override
  public boolean isWellFormedXML() {
    return literal.isWellFormedXML();
  }

  @Override
  public String getParseType() {
    return null;
  }

  @Override
  public String toString() {
    try {
      return literal.getString();
    } catch (JenaException e) {
      throw new OntopiaRuntimeException(e);
    }
  }

  @Override
  public String getLang() {
    return literal.getLanguage();
  }

  @Override
  public String getDatatypeURI() {
    return null;
  }

  @Override
  public void taint() {
    tainted = true;
  }

  @Override
  public boolean isTainted() {
    return tainted;
  }

}

