
// $Id: NormalizeWhitespaceTag.java,v 1.2 2006/05/05 12:15:11 grove Exp $

package net.ontopia.topicmaps.nav2.taglibs.tolog;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * INTERNAL: 
 */
public class NormalizeWhitespaceTag extends BodyTagSupport {

  public int doStartTag() {
    return EVAL_BODY_BUFFERED;
  }

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
    private boolean previousWasGT;
    private NormalizeWhitespaceWriter(Writer w) {
      this.w = w;
    }
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
          previousWasGT = true;
          w.write(c);
          break;
        default:
          //if (previousWasWS && !(previousWasGT && c == '<')) w.write(' '); // dangerous
          if (previousWasWS) w.write(' ');
          previousWasWS = false;
          previousWasGT = false;
          w.write(c);
        }
      }
    }
    public void flush() throws IOException {
      w.flush();
    }
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
