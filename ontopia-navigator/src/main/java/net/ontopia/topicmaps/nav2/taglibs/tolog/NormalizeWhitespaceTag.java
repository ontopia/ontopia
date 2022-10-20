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

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

/**
 * INTERNAL: 
 */
public class NormalizeWhitespaceTag extends BodyTagSupport {

  @Override
  public int doStartTag() {
    return EVAL_BODY_BUFFERED;
  }

  @Override
  public int doEndTag() throws JspException {
    BodyContent bc = getBodyContent();
    if (bc != null) {
      try {
        NormalizeWhitespaceWriter nww = new NormalizeWhitespaceWriter(getBodyContent().getEnclosingWriter());
        bc.writeOut(nww);
        // make sure orphaned whitespace is flushed
        nww.done();
      } catch (IOException e) {
        throw new JspException(e);
      }
    }
    return EVAL_PAGE;
  }

  private static class NormalizeWhitespaceWriter extends Writer {
    private Writer w;
    private boolean previousWasWS;
    private NormalizeWhitespaceWriter(Writer w) {
      this.w = w;
    }
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
      for (int i=0; i < len; i++) {
        char c = cbuf[off+i];
        switch (c) {
        case ' ': 
        case '\t':
        case '\n':
        case '\r':
          previousWasWS = true;
          break;
        case '>':
          w.write(c);
          break;
        default:
          //if (previousWasWS && !(previousWasGT && c == '<')) w.write(' '); // dangerous
          if (previousWasWS) {
            w.write(' ');
        }
          previousWasWS = false;
          w.write(c);
        }
      }
    }
    @Override
    public void flush() throws IOException {
      w.flush();
    }
    @Override
    public void close() throws IOException {
      done();
      w.close();
    }
    public void done() throws IOException{      
      if (previousWasWS) {
        w.write(' ');
        previousWasWS = false;
      }
    }
  }
  
}
