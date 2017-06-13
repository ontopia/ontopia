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
package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PUBLIC: A JSON serializer. Take a look at the <a
 * href="http://www.json.org/">JSON Homepage</a> for a complete specification of
 * the format.
 * 
 * The JSONWriter object provides <code>key</code>, <code>value</code> and
 * <code>pair</code> methods to append JSON key/value pairs. With the
 * <code>array</code> and <code>endArray</code> methods you can create bound
 * array values, and <code>object</code> and <code>endObject</code> methods
 * allows you to create and bound objects. All of these methods return the
 * JSONWriter instance, permitting a cascade style. For example,
 * 
 * <pre>
 * new JSONWriter(myWriter).object().key(&quot;JSON&quot;).value(&quot;Hello, World!&quot;)
 *     .endObject();
 * </pre>
 * 
 * which writes
 * 
 * <pre>
 * {"JSON":"Hello, World!"}
 * </pre>
 * 
 * <b>Note:</b> a default instance of a JSONWriter will prettify the output,
 * i.e. include whitespace and newlines as appropriate. To disable this
 * behaviour, use <a href="#setPrettify">setPrettify</a>.
 * 
 * @since 5.1
 */
public class JSONWriter {
  static Logger log = LoggerFactory.getLogger(JSONWriter.class.getName());

  private final static int INDENT = 2;

  private Writer out;
  private boolean prettify;
  private int depth;
  private boolean comma;
  private boolean startOfDocument;
  private boolean closeWriter = false;

  /**
   * PUBLIC: Create an JSONWriter that writes to a given OutputStream in UTF-8.
   * 
   * @param stream Where the output should be written.
   */
  public JSONWriter(OutputStream stream) throws IOException {
    this(stream, "utf-8");
  }

  /**
   * PUBLIC: Create an JSONWriter that writes to a given OutputStream in the
   * given encoding.
   * 
   * @param stream Where the output should be written.
   * @param encoding The desired character encoding.
   */
  public JSONWriter(OutputStream stream, String encoding) throws IOException {
    this(new OutputStreamWriter(stream, encoding));
  }

  /**
   * PUBLIC: Create an JSONWriter that writes to a given Writer.
   * 
   * @param out Where the output should be written.
   */
  public JSONWriter(Writer out) {
    this.out = out;
    this.prettify = true;
    this.depth = -1;
    this.comma = false;
    this.startOfDocument = true;
  }

  /**
   * PUBLIC: Returns whether the output from the JSON serializer is being
   * prettified, e.g. contains newlines and indentation.
   * 
   * @return <tt>true</tt> if the JSON output should be prettified,
   *         <tt>false</tt> otherwise.
   */
  public boolean isPrettify() {
    return prettify;
  }

  /**
   * PUBLIC: Sets the prettify behaviour of the JSON serializer.
   * 
   * @param prettify <tt>true</tt> to enable prettifying of the JSON output,
   *          <tt>false</tt> to disable it.
   */
  public void setPrettify(boolean prettify) {
    this.prettify = prettify;
  }

  /**
   * PUBLIC: Sets whether the writer should close the underlying IO when finished.
   */
  public void setCloseWriter(boolean closeWriter) {
    this.closeWriter = closeWriter;
  }

  /**
   * PUBLIC: Finish the serialization process, flushes the underlying stream.
   */
  public void finish() throws IOException {
    if (prettify) {
      out.write('\n');
    }
    out.flush();
    
    if (closeWriter) {
      out.close();
    }
  }
  
  /**
   * PUBLIC: Begin to append a new object.
   *  
   * @return this
   */
  public JSONWriter object() throws IOException {
    if (comma) {
      out.write(',');
    }
    depth++;
    indent();
    out.write('{');
    comma = false;
    startOfDocument = false;
    return this;
  }

  /**
   * PUBLIC: Finish of an JSON object.
   * 
   * @return this
   */
  public JSONWriter endObject() throws IOException {
    out.write('}');
    depth--;
    comma = true;
    return this;
  }

  /**
   * PUBLIC: Begin a new JSON array.
   * 
   * @return this
   */
  public JSONWriter array() throws IOException {
    out.write('[');
    depth++;
    comma = false;
    return this;
  }

  /**
   * PUBLIC: Finish an JSON array.
   * 
   * @return this
   */
  public JSONWriter endArray() throws IOException {
    out.write(']');
    depth--;
    comma = true;
    return this;
  }

  /**
   * PUBLIC: Write out the given key. The key is quoted and escaped according to
   * the JSON specification.
   * 
   * @param key The key to be written.
   * @return this
   */
  public JSONWriter key(String key) throws IOException {
    if (comma) {
      out.write(',');
      indent();
      out.write(' ');
    }
    out.write(quote(key));
    out.write(':');
    comma = false;
    return this;
  }

  /**
   * Write out the given value. The value is quoted and escaped according to the
   * JSON specification.
   * 
   * @param value The value to be written.
   * @return this
   */
  public JSONWriter value(String value) throws IOException {
    if (comma) {
      out.write(',');
    }
    out.write(quote(value));
    comma = true;
    return this;
  }

  /**
   * Write a complete JSON key/value pair to the stream.
   * This method is just for convenience, it does the same as
   * 
   * <pre>
   *   writer.key("mykey").value("myvalue");
   * </pre>
   * 
   * @param key The key to be written.
   * @param value The value to be written.
   * @return this
   */
  public JSONWriter pair(String key, String value) throws IOException {
    return this.key(key).value(value);
  }

  private void indent() throws IOException {
    if (prettify) {
      if (depth >= 0 && !startOfDocument) {
        out.write('\n');
      }
      
      if (depth > 0) {
        int indentation = depth * INDENT;
        out.write(String.format("%1$" + indentation + "s", ' '));
      }
    }
  }

  /**
   * Produce a string in double quotes with backslash sequences in all the right
   * places. A backslash will be inserted within </, allowing JSON text to be
   * delivered in HTML. In JSON text, a string cannot contain a control
   * character or an unescaped quote or backslash.
   * 
   * @param string A String
   * @return A String correctly formatted for insertion in a JSON text.
   * @author JSON.org
   */
  private static String quote(String string) {
    if (string == null || string.length() == 0) {
      return "\"\"";
    }

    char b;
    char c = 0;
    int i;
    int len = string.length();
    StringBuilder sb = new StringBuilder(len + 4);
    String t;

    sb.append('"');
    for (i = 0; i < len; i += 1) {
      b = c;
      c = string.charAt(i);
      switch (c) {
      case '\\':
      case '"':
        sb.append('\\');
        sb.append(c);
        break;
      case '/':
        if (b == '<') {
          sb.append('\\');
        }
        sb.append(c);
        break;
      case '\b':
        sb.append("\\b");
        break;
      case '\t':
        sb.append("\\t");
        break;
      case '\n':
        sb.append("\\n");
        break;
      case '\f':
        sb.append("\\f");
        break;
      case '\r':
        sb.append("\\r");
        break;
      default:
        if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
            || (c >= '\u2000' && c < '\u2100')) {
          t = "000" + Integer.toHexString(c);
          sb.append("\\u" + t.substring(t.length() - 4));
        } else {
          sb.append(c);
        }
      }
    }
    sb.append('"');
    return sb.toString();
  }
}
