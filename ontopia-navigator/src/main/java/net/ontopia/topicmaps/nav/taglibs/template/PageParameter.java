/*
 * #!
 * Ontopia Navigator
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

package net.ontopia.topicmaps.nav.taglibs.template;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;

/**
 * INTERNAL: Data carrier class used to pass data between tags.
 */
public class PageParameter implements Externalizable { 

  private String content;
  private boolean direct;

  public PageParameter(String content, boolean direct) {
    setContent(content);
    setDirect(direct);
  }

  public void setContent(String s) {
    content = s;
  }
  
  public void setDirect(boolean b) {
    direct = b;
  }

  public String getContent() {
    return content;
  }
  
  public boolean isDirect() {
    return direct;
  }
  
  // --------------------------------------------------------------------------
  // Externalization
  // --------------------------------------------------------------------------
  
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeUTF(content);
    out.writeBoolean(direct);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    content = in.readUTF();
    direct = in.readBoolean();
  }

}
