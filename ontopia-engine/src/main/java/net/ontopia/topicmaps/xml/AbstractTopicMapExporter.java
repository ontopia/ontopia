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

package net.ontopia.topicmaps.xml;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import org.xml.sax.helpers.AttributesImpl;
import net.ontopia.utils.CharacterSet;

/**
 * INTERNAL: Abstract class providing common operations used by XML
 * exporters.
 */
public abstract class AbstractTopicMapExporter {
  private static CharacterSet namestart = null;
  private static CharacterSet namechar = null;

  protected static final String EMPTY_NAMESPACE = "";
  protected static final String EMPTY_LOCALNAME = "";
  protected Predicate filter;
  
  /**
   * INTERNAL: Whether or not IDs should be added to all elements.
   */
  protected boolean add_ids = true;

  /**
   * PUBLIC: Sets the filter that decides which topic map constructs
   * are accepted in the exported TM/XML. Uses 'filter' to identify
   * individual topic constructs as allowed or disallowed. TM
   * constructs that depend on the disallowed topics are also
   * disallowed.   
   * @param filter Places constraints on individual topicmap constructs.
   * @since 3.2
   */
  public void setFilter(Predicate filter) {

    // WARNING: Do NOT use the TMExporterDecider or any other kind of
    // wrapper around the filter here. Filters should be used raw. If
    // any client code wants a utility wrapper, let them set it up
    // explicitly.

    this.filter = filter;
  }
  /**
   * Filter a whole collection of objects.
   * @param unfiltered The objects to filter.
   * @return A new collection containing all objects accepted by the filter, or
   *         if this.filter is null, returns the original collection.
   */
  protected Collection filterCollection(Collection unfiltered) {
    if (filter == null) {
      return unfiltered;
    }
    
    Collection retVal = new ArrayList();
    Iterator unfilteredIt = unfiltered.iterator();
    while (unfilteredIt.hasNext()) {
      Object current = unfilteredIt.next();
      if (filter.test(current)) {
        retVal.add(current);
      }
    }
    return retVal;
  }
  
  /**
   * INTERNAL: Filter a single object.
   * @param unfiltered The object to filter.
   * @return True if the object is accepted by the filter or the filter is null.
   *         False otherwise.
   */
  protected boolean filterOk(Object unfiltered) {
    if (filter == null) {
      return true;
    }
    return filter.test(unfiltered);
  }
  
  protected void addId(AttributesImpl atts, TMObjectIF tmobject) {
    String id = getElementId(tmobject);
    if (id != null) {
      atts.addAttribute(EMPTY_NAMESPACE, EMPTY_LOCALNAME,  "id", "ID", id);
    }
  }
  
  /**
   * INTERNAL: Method used to extract the XTM element id from a topic
   * map object when a source locator relative to the topic map base
   * address exists.
   *
   * <p>This method is critical because we want to preserve the
   * connection between subject indicators used to reify local objects,
   * and at the same time we wish to keep the symbolic IDs often used
   * in XTM and LTM files.
   *
   * <p>When setting the IDs of elements we set the ID to "fragment"
   * if the object has a source locator of the form "base#fragment",
   * where base is the base address of the topic map store. If no such
   * source locator is found, the ID becomes "id" + object ID.
   *
   * <p>See the getSubjectIndicatorRef method in XTMTopicMapExporter
   * to see how subject indicators are exported. Synchronization of
   * these two methods is vital.
   */
  public String getElementId(TMObjectIF tmobject) {    
    if (tmobject instanceof ReifiableIF && ((ReifiableIF)tmobject).getReifier() != null) {
      return "reified-id" + ((ReifiableIF)tmobject).getReifier().getObjectId();
    }

    if (!(add_ids || (tmobject instanceof TopicIF))) {
      return null; // don't output an ID
    }

    String id = null;
    TopicMapIF tm = tmobject.getTopicMap();
    LocatorIF baseloc =
      (tm == null ? null : tm.getStore().getBaseAddress());
    
    if (baseloc != null) {
      String base = baseloc.getAddress();
      Iterator it = tmobject.getItemIdentifiers().iterator();
      while (it.hasNext()) {
        LocatorIF sloc = (LocatorIF) it.next();
        if (sloc.getAddress().startsWith(base)) {
          String addr = sloc.getAddress();
          int pos = addr.indexOf('#');
          if (pos != -1) {
            id = addr.substring(pos + 1);
            if (mayCollide(id) || !isValidXMLId(id)) {
              // reject this ID if it may collide with object ID-based ones
              id = null;
            } else {
              break;
            }
          }
        }
      }
    }

    if (id == null) {
      id = "id" + tmobject.getObjectId();
    }

    return id;
  }

  // --- Internal methods

  /**
   * INTERNAL: Used to find out whether this ID may collide with IDs
   * produced from object IDs. See bug #654. Works for both id234234
   * and idT234234.
   */
  static public boolean mayCollide(String id) {
    if (!(id.startsWith("id") && id.length() > 2)) {
      return false;
    }

    int ix = 2;
    if (id.charAt(ix) >= 'A' && id.charAt(ix) <= 'Z') {
      ix++; // we accept an uppercase character in this position
    }

    // the rest must be digits
    for (; ix < id.length(); ix++) {
      if (id.charAt(ix) < '0' || id.charAt(ix) > '9') {
        return false;
      }
    }
    return true;
  }

  /**
   * INTERNAL: Used to test whether the ID is a syntactically valid
   * XML ID.
   */
  protected boolean isValidXMLId(String id) {
    // [5]  Name     ::= (Letter | '_' | ':') (NameChar)*
    // [84] Letter   ::= BaseChar | Ideographic
    // [4]  NameChar ::= Letter | Digit | '.' | '-' | '_' | ':' | CombiningChar |
    //                   Extender

    if (namestart == null) {
      buildSets();
    }

    if (id.length() < 1 || !namestart.contains(id.charAt(0))) {
      return false;
    }

    for (int ix = 1; ix < id.length(); ix++) {
      if (!namechar.contains(id.charAt(ix))) {
        return false;
      }
    }

    return true;
  }

  private static void buildSets() {
    // this Java code autogenerated by src/python/lmg/xml-names/mkcode.py
    // DO NOT MODIFY HERE!
    namestart = new CharacterSet();
    namestart.addInterval((char) 0x003A, (char) 0x003A);
    namestart.addInterval((char) 0x0041, (char) 0x005A);
    namestart.addInterval((char) 0x005F, (char) 0x005F);
    namestart.addInterval((char) 0x0061, (char) 0x007A);
    namestart.addInterval((char) 0x00C0, (char) 0x00D6);
    namestart.addInterval((char) 0x00D8, (char) 0x00F6);
    namestart.addInterval((char) 0x00F8, (char) 0x00FF);
    namestart.addInterval((char) 0x0100, (char) 0x0131);
    namestart.addInterval((char) 0x0134, (char) 0x013E);
    namestart.addInterval((char) 0x0141, (char) 0x0148);
    namestart.addInterval((char) 0x014A, (char) 0x017E);
    namestart.addInterval((char) 0x0180, (char) 0x01C3);
    namestart.addInterval((char) 0x01CD, (char) 0x01F0);
    namestart.addInterval((char) 0x01F4, (char) 0x01F5);
    namestart.addInterval((char) 0x01FA, (char) 0x0217);
    namestart.addInterval((char) 0x0250, (char) 0x02A8);
    namestart.addInterval((char) 0x02BB, (char) 0x02C1);
    namestart.addInterval((char) 0x0386, (char) 0x0386);
    namestart.addInterval((char) 0x0388, (char) 0x038A);
    namestart.addInterval((char) 0x038C, (char) 0x038C);
    namestart.addInterval((char) 0x038E, (char) 0x03A1);
    namestart.addInterval((char) 0x03A3, (char) 0x03CE);
    namestart.addInterval((char) 0x03D0, (char) 0x03D6);
    namestart.addInterval((char) 0x03DA, (char) 0x03DA);
    namestart.addInterval((char) 0x03DC, (char) 0x03DC);
    namestart.addInterval((char) 0x03DE, (char) 0x03DE);
    namestart.addInterval((char) 0x03E0, (char) 0x03E0);
    namestart.addInterval((char) 0x03E2, (char) 0x03F3);
    namestart.addInterval((char) 0x0401, (char) 0x040C);
    namestart.addInterval((char) 0x040E, (char) 0x044F);
    namestart.addInterval((char) 0x0451, (char) 0x045C);
    namestart.addInterval((char) 0x045E, (char) 0x0481);
    namestart.addInterval((char) 0x0490, (char) 0x04C4);
    namestart.addInterval((char) 0x04C7, (char) 0x04C8);
    namestart.addInterval((char) 0x04CB, (char) 0x04CC);
    namestart.addInterval((char) 0x04D0, (char) 0x04EB);
    namestart.addInterval((char) 0x04EE, (char) 0x04F5);
    namestart.addInterval((char) 0x04F8, (char) 0x04F9);
    namestart.addInterval((char) 0x0531, (char) 0x0556);
    namestart.addInterval((char) 0x0559, (char) 0x0559);
    namestart.addInterval((char) 0x0561, (char) 0x0586);
    namestart.addInterval((char) 0x05D0, (char) 0x05EA);
    namestart.addInterval((char) 0x05F0, (char) 0x05F2);
    namestart.addInterval((char) 0x0621, (char) 0x063A);
    namestart.addInterval((char) 0x0641, (char) 0x064A);
    namestart.addInterval((char) 0x0671, (char) 0x06B7);
    namestart.addInterval((char) 0x06BA, (char) 0x06BE);
    namestart.addInterval((char) 0x06C0, (char) 0x06CE);
    namestart.addInterval((char) 0x06D0, (char) 0x06D3);
    namestart.addInterval((char) 0x06D5, (char) 0x06D5);
    namestart.addInterval((char) 0x06E5, (char) 0x06E6);
    namestart.addInterval((char) 0x0905, (char) 0x0939);
    namestart.addInterval((char) 0x093D, (char) 0x093D);
    namestart.addInterval((char) 0x0958, (char) 0x0961);
    namestart.addInterval((char) 0x0985, (char) 0x098C);
    namestart.addInterval((char) 0x098F, (char) 0x0990);
    namestart.addInterval((char) 0x0993, (char) 0x09A8);
    namestart.addInterval((char) 0x09AA, (char) 0x09B0);
    namestart.addInterval((char) 0x09B2, (char) 0x09B2);
    namestart.addInterval((char) 0x09B6, (char) 0x09B9);
    namestart.addInterval((char) 0x09DC, (char) 0x09DD);
    namestart.addInterval((char) 0x09DF, (char) 0x09E1);
    namestart.addInterval((char) 0x09F0, (char) 0x09F1);
    namestart.addInterval((char) 0x0A05, (char) 0x0A0A);
    namestart.addInterval((char) 0x0A0F, (char) 0x0A10);
    namestart.addInterval((char) 0x0A13, (char) 0x0A28);
    namestart.addInterval((char) 0x0A2A, (char) 0x0A30);
    namestart.addInterval((char) 0x0A32, (char) 0x0A33);
    namestart.addInterval((char) 0x0A35, (char) 0x0A36);
    namestart.addInterval((char) 0x0A38, (char) 0x0A39);
    namestart.addInterval((char) 0x0A59, (char) 0x0A5C);
    namestart.addInterval((char) 0x0A5E, (char) 0x0A5E);
    namestart.addInterval((char) 0x0A72, (char) 0x0A74);
    namestart.addInterval((char) 0x0A85, (char) 0x0A8B);
    namestart.addInterval((char) 0x0A8D, (char) 0x0A8D);
    namestart.addInterval((char) 0x0A8F, (char) 0x0A91);
    namestart.addInterval((char) 0x0A93, (char) 0x0AA8);
    namestart.addInterval((char) 0x0AAA, (char) 0x0AB0);
    namestart.addInterval((char) 0x0AB2, (char) 0x0AB3);
    namestart.addInterval((char) 0x0AB5, (char) 0x0AB9);
    namestart.addInterval((char) 0x0ABD, (char) 0x0ABD);
    namestart.addInterval((char) 0x0AE0, (char) 0x0AE0);
    namestart.addInterval((char) 0x0B05, (char) 0x0B0C);
    namestart.addInterval((char) 0x0B0F, (char) 0x0B10);
    namestart.addInterval((char) 0x0B13, (char) 0x0B28);
    namestart.addInterval((char) 0x0B2A, (char) 0x0B30);
    namestart.addInterval((char) 0x0B32, (char) 0x0B33);
    namestart.addInterval((char) 0x0B36, (char) 0x0B39);
    namestart.addInterval((char) 0x0B3D, (char) 0x0B3D);
    namestart.addInterval((char) 0x0B5C, (char) 0x0B5D);
    namestart.addInterval((char) 0x0B5F, (char) 0x0B61);
    namestart.addInterval((char) 0x0B85, (char) 0x0B8A);
    namestart.addInterval((char) 0x0B8E, (char) 0x0B90);
    namestart.addInterval((char) 0x0B92, (char) 0x0B95);
    namestart.addInterval((char) 0x0B99, (char) 0x0B9A);
    namestart.addInterval((char) 0x0B9C, (char) 0x0B9C);
    namestart.addInterval((char) 0x0B9E, (char) 0x0B9F);
    namestart.addInterval((char) 0x0BA3, (char) 0x0BA4);
    namestart.addInterval((char) 0x0BA8, (char) 0x0BAA);
    namestart.addInterval((char) 0x0BAE, (char) 0x0BB5);
    namestart.addInterval((char) 0x0BB7, (char) 0x0BB9);
    namestart.addInterval((char) 0x0C05, (char) 0x0C0C);
    namestart.addInterval((char) 0x0C0E, (char) 0x0C10);
    namestart.addInterval((char) 0x0C12, (char) 0x0C28);
    namestart.addInterval((char) 0x0C2A, (char) 0x0C33);
    namestart.addInterval((char) 0x0C35, (char) 0x0C39);
    namestart.addInterval((char) 0x0C60, (char) 0x0C61);
    namestart.addInterval((char) 0x0C85, (char) 0x0C8C);
    namestart.addInterval((char) 0x0C8E, (char) 0x0C90);
    namestart.addInterval((char) 0x0C92, (char) 0x0CA8);
    namestart.addInterval((char) 0x0CAA, (char) 0x0CB3);
    namestart.addInterval((char) 0x0CB5, (char) 0x0CB9);
    namestart.addInterval((char) 0x0CDE, (char) 0x0CDE);
    namestart.addInterval((char) 0x0CE0, (char) 0x0CE1);
    namestart.addInterval((char) 0x0D05, (char) 0x0D0C);
    namestart.addInterval((char) 0x0D0E, (char) 0x0D10);
    namestart.addInterval((char) 0x0D12, (char) 0x0D28);
    namestart.addInterval((char) 0x0D2A, (char) 0x0D39);
    namestart.addInterval((char) 0x0D60, (char) 0x0D61);
    namestart.addInterval((char) 0x0E01, (char) 0x0E2E);
    namestart.addInterval((char) 0x0E30, (char) 0x0E30);
    namestart.addInterval((char) 0x0E32, (char) 0x0E33);
    namestart.addInterval((char) 0x0E40, (char) 0x0E45);
    namestart.addInterval((char) 0x0E81, (char) 0x0E82);
    namestart.addInterval((char) 0x0E84, (char) 0x0E84);
    namestart.addInterval((char) 0x0E87, (char) 0x0E88);
    namestart.addInterval((char) 0x0E8A, (char) 0x0E8A);
    namestart.addInterval((char) 0x0E8D, (char) 0x0E8D);
    namestart.addInterval((char) 0x0E94, (char) 0x0E97);
    namestart.addInterval((char) 0x0E99, (char) 0x0E9F);
    namestart.addInterval((char) 0x0EA1, (char) 0x0EA3);
    namestart.addInterval((char) 0x0EA5, (char) 0x0EA5);
    namestart.addInterval((char) 0x0EA7, (char) 0x0EA7);
    namestart.addInterval((char) 0x0EAA, (char) 0x0EAB);
    namestart.addInterval((char) 0x0EAD, (char) 0x0EAE);
    namestart.addInterval((char) 0x0EB0, (char) 0x0EB0);
    namestart.addInterval((char) 0x0EB2, (char) 0x0EB3);
    namestart.addInterval((char) 0x0EBD, (char) 0x0EBD);
    namestart.addInterval((char) 0x0EC0, (char) 0x0EC4);
    namestart.addInterval((char) 0x0F40, (char) 0x0F47);
    namestart.addInterval((char) 0x0F49, (char) 0x0F69);
    namestart.addInterval((char) 0x10A0, (char) 0x10C5);
    namestart.addInterval((char) 0x10D0, (char) 0x10F6);
    namestart.addInterval((char) 0x1100, (char) 0x1100);
    namestart.addInterval((char) 0x1102, (char) 0x1103);
    namestart.addInterval((char) 0x1105, (char) 0x1107);
    namestart.addInterval((char) 0x1109, (char) 0x1109);
    namestart.addInterval((char) 0x110B, (char) 0x110C);
    namestart.addInterval((char) 0x110E, (char) 0x1112);
    namestart.addInterval((char) 0x113C, (char) 0x113C);
    namestart.addInterval((char) 0x113E, (char) 0x113E);
    namestart.addInterval((char) 0x1140, (char) 0x1140);
    namestart.addInterval((char) 0x114C, (char) 0x114C);
    namestart.addInterval((char) 0x114E, (char) 0x114E);
    namestart.addInterval((char) 0x1150, (char) 0x1150);
    namestart.addInterval((char) 0x1154, (char) 0x1155);
    namestart.addInterval((char) 0x1159, (char) 0x1159);
    namestart.addInterval((char) 0x115F, (char) 0x1161);
    namestart.addInterval((char) 0x1163, (char) 0x1163);
    namestart.addInterval((char) 0x1165, (char) 0x1165);
    namestart.addInterval((char) 0x1167, (char) 0x1167);
    namestart.addInterval((char) 0x1169, (char) 0x1169);
    namestart.addInterval((char) 0x116D, (char) 0x116E);
    namestart.addInterval((char) 0x1172, (char) 0x1173);
    namestart.addInterval((char) 0x1175, (char) 0x1175);
    namestart.addInterval((char) 0x119E, (char) 0x119E);
    namestart.addInterval((char) 0x11A8, (char) 0x11A8);
    namestart.addInterval((char) 0x11AB, (char) 0x11AB);
    namestart.addInterval((char) 0x11AE, (char) 0x11AF);
    namestart.addInterval((char) 0x11B7, (char) 0x11B8);
    namestart.addInterval((char) 0x11BA, (char) 0x11BA);
    namestart.addInterval((char) 0x11BC, (char) 0x11C2);
    namestart.addInterval((char) 0x11EB, (char) 0x11EB);
    namestart.addInterval((char) 0x11F0, (char) 0x11F0);
    namestart.addInterval((char) 0x11F9, (char) 0x11F9);
    namestart.addInterval((char) 0x1E00, (char) 0x1E9B);
    namestart.addInterval((char) 0x1EA0, (char) 0x1EF9);
    namestart.addInterval((char) 0x1F00, (char) 0x1F15);
    namestart.addInterval((char) 0x1F18, (char) 0x1F1D);
    namestart.addInterval((char) 0x1F20, (char) 0x1F45);
    namestart.addInterval((char) 0x1F48, (char) 0x1F4D);
    namestart.addInterval((char) 0x1F50, (char) 0x1F57);
    namestart.addInterval((char) 0x1F59, (char) 0x1F59);
    namestart.addInterval((char) 0x1F5B, (char) 0x1F5B);
    namestart.addInterval((char) 0x1F5D, (char) 0x1F5D);
    namestart.addInterval((char) 0x1F5F, (char) 0x1F7D);
    namestart.addInterval((char) 0x1F80, (char) 0x1FB4);
    namestart.addInterval((char) 0x1FB6, (char) 0x1FBC);
    namestart.addInterval((char) 0x1FBE, (char) 0x1FBE);
    namestart.addInterval((char) 0x1FC2, (char) 0x1FC4);
    namestart.addInterval((char) 0x1FC6, (char) 0x1FCC);
    namestart.addInterval((char) 0x1FD0, (char) 0x1FD3);
    namestart.addInterval((char) 0x1FD6, (char) 0x1FDB);
    namestart.addInterval((char) 0x1FE0, (char) 0x1FEC);
    namestart.addInterval((char) 0x1FF2, (char) 0x1FF4);
    namestart.addInterval((char) 0x1FF6, (char) 0x1FFC);
    namestart.addInterval((char) 0x2126, (char) 0x2126);
    namestart.addInterval((char) 0x212A, (char) 0x212B);
    namestart.addInterval((char) 0x212E, (char) 0x212E);
    namestart.addInterval((char) 0x2180, (char) 0x2182);
    namestart.addInterval((char) 0x3007, (char) 0x3007);
    namestart.addInterval((char) 0x3021, (char) 0x3029);
    namestart.addInterval((char) 0x3041, (char) 0x3094);
    namestart.addInterval((char) 0x30A1, (char) 0x30FA);
    namestart.addInterval((char) 0x3105, (char) 0x312C);
    namestart.addInterval((char) 0x4E00, (char) 0x9FA5);
    namestart.addInterval((char) 0xAC00, (char) 0xD7A3);
    namestart.close();
    
    namechar = new CharacterSet();
    namechar.addInterval((char) 0x002D, (char) 0x002E);
    namechar.addInterval((char) 0x0030, (char) 0x0039);
    namechar.addInterval((char) 0x003A, (char) 0x003A);
    namechar.addInterval((char) 0x0041, (char) 0x005A);
    namechar.addInterval((char) 0x005F, (char) 0x005F);
    namechar.addInterval((char) 0x0061, (char) 0x007A);
    namechar.addInterval((char) 0x00B7, (char) 0x00B7);
    namechar.addInterval((char) 0x00C0, (char) 0x00D6);
    namechar.addInterval((char) 0x00D8, (char) 0x00F6);
    namechar.addInterval((char) 0x00F8, (char) 0x00FF);
    namechar.addInterval((char) 0x0100, (char) 0x0131);
    namechar.addInterval((char) 0x0134, (char) 0x013E);
    namechar.addInterval((char) 0x0141, (char) 0x0148);
    namechar.addInterval((char) 0x014A, (char) 0x017E);
    namechar.addInterval((char) 0x0180, (char) 0x01C3);
    namechar.addInterval((char) 0x01CD, (char) 0x01F0);
    namechar.addInterval((char) 0x01F4, (char) 0x01F5);
    namechar.addInterval((char) 0x01FA, (char) 0x0217);
    namechar.addInterval((char) 0x0250, (char) 0x02A8);
    namechar.addInterval((char) 0x02BB, (char) 0x02C1);
    namechar.addInterval((char) 0x02D0, (char) 0x02D0);
    namechar.addInterval((char) 0x02D1, (char) 0x02D1);
    namechar.addInterval((char) 0x0300, (char) 0x0345);
    namechar.addInterval((char) 0x0360, (char) 0x0361);
    namechar.addInterval((char) 0x0386, (char) 0x0386);
    namechar.addInterval((char) 0x0387, (char) 0x0387);
    namechar.addInterval((char) 0x0388, (char) 0x038A);
    namechar.addInterval((char) 0x038C, (char) 0x038C);
    namechar.addInterval((char) 0x038E, (char) 0x03A1);
    namechar.addInterval((char) 0x03A3, (char) 0x03CE);
    namechar.addInterval((char) 0x03D0, (char) 0x03D6);
    namechar.addInterval((char) 0x03DA, (char) 0x03DA);
    namechar.addInterval((char) 0x03DC, (char) 0x03DC);
    namechar.addInterval((char) 0x03DE, (char) 0x03DE);
    namechar.addInterval((char) 0x03E0, (char) 0x03E0);
    namechar.addInterval((char) 0x03E2, (char) 0x03F3);
    namechar.addInterval((char) 0x0401, (char) 0x040C);
    namechar.addInterval((char) 0x040E, (char) 0x044F);
    namechar.addInterval((char) 0x0451, (char) 0x045C);
    namechar.addInterval((char) 0x045E, (char) 0x0481);
    namechar.addInterval((char) 0x0483, (char) 0x0486);
    namechar.addInterval((char) 0x0490, (char) 0x04C4);
    namechar.addInterval((char) 0x04C7, (char) 0x04C8);
    namechar.addInterval((char) 0x04CB, (char) 0x04CC);
    namechar.addInterval((char) 0x04D0, (char) 0x04EB);
    namechar.addInterval((char) 0x04EE, (char) 0x04F5);
    namechar.addInterval((char) 0x04F8, (char) 0x04F9);
    namechar.addInterval((char) 0x0531, (char) 0x0556);
    namechar.addInterval((char) 0x0559, (char) 0x0559);
    namechar.addInterval((char) 0x0561, (char) 0x0586);
    namechar.addInterval((char) 0x0591, (char) 0x05A1);
    namechar.addInterval((char) 0x05A3, (char) 0x05B9);
    namechar.addInterval((char) 0x05BB, (char) 0x05BD);
    namechar.addInterval((char) 0x05BF, (char) 0x05BF);
    namechar.addInterval((char) 0x05C1, (char) 0x05C2);
    namechar.addInterval((char) 0x05C4, (char) 0x05C4);
    namechar.addInterval((char) 0x05D0, (char) 0x05EA);
    namechar.addInterval((char) 0x05F0, (char) 0x05F2);
    namechar.addInterval((char) 0x0621, (char) 0x063A);
    namechar.addInterval((char) 0x0640, (char) 0x0640);
    namechar.addInterval((char) 0x0641, (char) 0x064A);
    namechar.addInterval((char) 0x064B, (char) 0x0652);
    namechar.addInterval((char) 0x0660, (char) 0x0669);
    namechar.addInterval((char) 0x0670, (char) 0x0670);
    namechar.addInterval((char) 0x0671, (char) 0x06B7);
    namechar.addInterval((char) 0x06BA, (char) 0x06BE);
    namechar.addInterval((char) 0x06C0, (char) 0x06CE);
    namechar.addInterval((char) 0x06D0, (char) 0x06D3);
    namechar.addInterval((char) 0x06D5, (char) 0x06D5);
    namechar.addInterval((char) 0x06D6, (char) 0x06DC);
    namechar.addInterval((char) 0x06DD, (char) 0x06DF);
    namechar.addInterval((char) 0x06E0, (char) 0x06E4);
    namechar.addInterval((char) 0x06E5, (char) 0x06E6);
    namechar.addInterval((char) 0x06E7, (char) 0x06E8);
    namechar.addInterval((char) 0x06EA, (char) 0x06ED);
    namechar.addInterval((char) 0x06F0, (char) 0x06F9);
    namechar.addInterval((char) 0x0901, (char) 0x0903);
    namechar.addInterval((char) 0x0905, (char) 0x0939);
    namechar.addInterval((char) 0x093C, (char) 0x093C);
    namechar.addInterval((char) 0x093D, (char) 0x093D);
    namechar.addInterval((char) 0x093E, (char) 0x094C);
    namechar.addInterval((char) 0x094D, (char) 0x094D);
    namechar.addInterval((char) 0x0951, (char) 0x0954);
    namechar.addInterval((char) 0x0958, (char) 0x0961);
    namechar.addInterval((char) 0x0962, (char) 0x0963);
    namechar.addInterval((char) 0x0966, (char) 0x096F);
    namechar.addInterval((char) 0x0981, (char) 0x0983);
    namechar.addInterval((char) 0x0985, (char) 0x098C);
    namechar.addInterval((char) 0x098F, (char) 0x0990);
    namechar.addInterval((char) 0x0993, (char) 0x09A8);
    namechar.addInterval((char) 0x09AA, (char) 0x09B0);
    namechar.addInterval((char) 0x09B2, (char) 0x09B2);
    namechar.addInterval((char) 0x09B6, (char) 0x09B9);
    namechar.addInterval((char) 0x09BC, (char) 0x09BC);
    namechar.addInterval((char) 0x09BE, (char) 0x09BE);
    namechar.addInterval((char) 0x09BF, (char) 0x09BF);
    namechar.addInterval((char) 0x09C0, (char) 0x09C4);
    namechar.addInterval((char) 0x09C7, (char) 0x09C8);
    namechar.addInterval((char) 0x09CB, (char) 0x09CD);
    namechar.addInterval((char) 0x09D7, (char) 0x09D7);
    namechar.addInterval((char) 0x09DC, (char) 0x09DD);
    namechar.addInterval((char) 0x09DF, (char) 0x09E1);
    namechar.addInterval((char) 0x09E2, (char) 0x09E3);
    namechar.addInterval((char) 0x09E6, (char) 0x09EF);
    namechar.addInterval((char) 0x09F0, (char) 0x09F1);
    namechar.addInterval((char) 0x0A02, (char) 0x0A02);
    namechar.addInterval((char) 0x0A05, (char) 0x0A0A);
    namechar.addInterval((char) 0x0A0F, (char) 0x0A10);
    namechar.addInterval((char) 0x0A13, (char) 0x0A28);
    namechar.addInterval((char) 0x0A2A, (char) 0x0A30);
    namechar.addInterval((char) 0x0A32, (char) 0x0A33);
    namechar.addInterval((char) 0x0A35, (char) 0x0A36);
    namechar.addInterval((char) 0x0A38, (char) 0x0A39);
    namechar.addInterval((char) 0x0A3C, (char) 0x0A3C);
    namechar.addInterval((char) 0x0A3E, (char) 0x0A3E);
    namechar.addInterval((char) 0x0A3F, (char) 0x0A3F);
    namechar.addInterval((char) 0x0A40, (char) 0x0A42);
    namechar.addInterval((char) 0x0A47, (char) 0x0A48);
    namechar.addInterval((char) 0x0A4B, (char) 0x0A4D);
    namechar.addInterval((char) 0x0A59, (char) 0x0A5C);
    namechar.addInterval((char) 0x0A5E, (char) 0x0A5E);
    namechar.addInterval((char) 0x0A66, (char) 0x0A6F);
    namechar.addInterval((char) 0x0A70, (char) 0x0A71);
    namechar.addInterval((char) 0x0A72, (char) 0x0A74);
    namechar.addInterval((char) 0x0A81, (char) 0x0A83);
    namechar.addInterval((char) 0x0A85, (char) 0x0A8B);
    namechar.addInterval((char) 0x0A8D, (char) 0x0A8D);
    namechar.addInterval((char) 0x0A8F, (char) 0x0A91);
    namechar.addInterval((char) 0x0A93, (char) 0x0AA8);
    namechar.addInterval((char) 0x0AAA, (char) 0x0AB0);
    namechar.addInterval((char) 0x0AB2, (char) 0x0AB3);
    namechar.addInterval((char) 0x0AB5, (char) 0x0AB9);
    namechar.addInterval((char) 0x0ABC, (char) 0x0ABC);
    namechar.addInterval((char) 0x0ABD, (char) 0x0ABD);
    namechar.addInterval((char) 0x0ABE, (char) 0x0AC5);
    namechar.addInterval((char) 0x0AC7, (char) 0x0AC9);
    namechar.addInterval((char) 0x0ACB, (char) 0x0ACD);
    namechar.addInterval((char) 0x0AE0, (char) 0x0AE0);
    namechar.addInterval((char) 0x0AE6, (char) 0x0AEF);
    namechar.addInterval((char) 0x0B01, (char) 0x0B03);
    namechar.addInterval((char) 0x0B05, (char) 0x0B0C);
    namechar.addInterval((char) 0x0B0F, (char) 0x0B10);
    namechar.addInterval((char) 0x0B13, (char) 0x0B28);
    namechar.addInterval((char) 0x0B2A, (char) 0x0B30);
    namechar.addInterval((char) 0x0B32, (char) 0x0B33);
    namechar.addInterval((char) 0x0B36, (char) 0x0B39);
    namechar.addInterval((char) 0x0B3C, (char) 0x0B3C);
    namechar.addInterval((char) 0x0B3D, (char) 0x0B3D);
    namechar.addInterval((char) 0x0B3E, (char) 0x0B43);
    namechar.addInterval((char) 0x0B47, (char) 0x0B48);
    namechar.addInterval((char) 0x0B4B, (char) 0x0B4D);
    namechar.addInterval((char) 0x0B56, (char) 0x0B57);
    namechar.addInterval((char) 0x0B5C, (char) 0x0B5D);
    namechar.addInterval((char) 0x0B5F, (char) 0x0B61);
    namechar.addInterval((char) 0x0B66, (char) 0x0B6F);
    namechar.addInterval((char) 0x0B82, (char) 0x0B83);
    namechar.addInterval((char) 0x0B85, (char) 0x0B8A);
    namechar.addInterval((char) 0x0B8E, (char) 0x0B90);
    namechar.addInterval((char) 0x0B92, (char) 0x0B95);
    namechar.addInterval((char) 0x0B99, (char) 0x0B9A);
    namechar.addInterval((char) 0x0B9C, (char) 0x0B9C);
    namechar.addInterval((char) 0x0B9E, (char) 0x0B9F);
    namechar.addInterval((char) 0x0BA3, (char) 0x0BA4);
    namechar.addInterval((char) 0x0BA8, (char) 0x0BAA);
    namechar.addInterval((char) 0x0BAE, (char) 0x0BB5);
    namechar.addInterval((char) 0x0BB7, (char) 0x0BB9);
    namechar.addInterval((char) 0x0BBE, (char) 0x0BC2);
    namechar.addInterval((char) 0x0BC6, (char) 0x0BC8);
    namechar.addInterval((char) 0x0BCA, (char) 0x0BCD);
    namechar.addInterval((char) 0x0BD7, (char) 0x0BD7);
    namechar.addInterval((char) 0x0BE7, (char) 0x0BEF);
    namechar.addInterval((char) 0x0C01, (char) 0x0C03);
    namechar.addInterval((char) 0x0C05, (char) 0x0C0C);
    namechar.addInterval((char) 0x0C0E, (char) 0x0C10);
    namechar.addInterval((char) 0x0C12, (char) 0x0C28);
    namechar.addInterval((char) 0x0C2A, (char) 0x0C33);
    namechar.addInterval((char) 0x0C35, (char) 0x0C39);
    namechar.addInterval((char) 0x0C3E, (char) 0x0C44);
    namechar.addInterval((char) 0x0C46, (char) 0x0C48);
    namechar.addInterval((char) 0x0C4A, (char) 0x0C4D);
    namechar.addInterval((char) 0x0C55, (char) 0x0C56);
    namechar.addInterval((char) 0x0C60, (char) 0x0C61);
    namechar.addInterval((char) 0x0C66, (char) 0x0C6F);
    namechar.addInterval((char) 0x0C82, (char) 0x0C83);
    namechar.addInterval((char) 0x0C85, (char) 0x0C8C);
    namechar.addInterval((char) 0x0C8E, (char) 0x0C90);
    namechar.addInterval((char) 0x0C92, (char) 0x0CA8);
    namechar.addInterval((char) 0x0CAA, (char) 0x0CB3);
    namechar.addInterval((char) 0x0CB5, (char) 0x0CB9);
    namechar.addInterval((char) 0x0CBE, (char) 0x0CC4);
    namechar.addInterval((char) 0x0CC6, (char) 0x0CC8);
    namechar.addInterval((char) 0x0CCA, (char) 0x0CCD);
    namechar.addInterval((char) 0x0CD5, (char) 0x0CD6);
    namechar.addInterval((char) 0x0CDE, (char) 0x0CDE);
    namechar.addInterval((char) 0x0CE0, (char) 0x0CE1);
    namechar.addInterval((char) 0x0CE6, (char) 0x0CEF);
    namechar.addInterval((char) 0x0D02, (char) 0x0D03);
    namechar.addInterval((char) 0x0D05, (char) 0x0D0C);
    namechar.addInterval((char) 0x0D0E, (char) 0x0D10);
    namechar.addInterval((char) 0x0D12, (char) 0x0D28);
    namechar.addInterval((char) 0x0D2A, (char) 0x0D39);
    namechar.addInterval((char) 0x0D3E, (char) 0x0D43);
    namechar.addInterval((char) 0x0D46, (char) 0x0D48);
    namechar.addInterval((char) 0x0D4A, (char) 0x0D4D);
    namechar.addInterval((char) 0x0D57, (char) 0x0D57);
    namechar.addInterval((char) 0x0D60, (char) 0x0D61);
    namechar.addInterval((char) 0x0D66, (char) 0x0D6F);
    namechar.addInterval((char) 0x0E01, (char) 0x0E2E);
    namechar.addInterval((char) 0x0E30, (char) 0x0E30);
    namechar.addInterval((char) 0x0E31, (char) 0x0E31);
    namechar.addInterval((char) 0x0E32, (char) 0x0E33);
    namechar.addInterval((char) 0x0E34, (char) 0x0E3A);
    namechar.addInterval((char) 0x0E40, (char) 0x0E45);
    namechar.addInterval((char) 0x0E46, (char) 0x0E46);
    namechar.addInterval((char) 0x0E47, (char) 0x0E4E);
    namechar.addInterval((char) 0x0E50, (char) 0x0E59);
    namechar.addInterval((char) 0x0E81, (char) 0x0E82);
    namechar.addInterval((char) 0x0E84, (char) 0x0E84);
    namechar.addInterval((char) 0x0E87, (char) 0x0E88);
    namechar.addInterval((char) 0x0E8A, (char) 0x0E8A);
    namechar.addInterval((char) 0x0E8D, (char) 0x0E8D);
    namechar.addInterval((char) 0x0E94, (char) 0x0E97);
    namechar.addInterval((char) 0x0E99, (char) 0x0E9F);
    namechar.addInterval((char) 0x0EA1, (char) 0x0EA3);
    namechar.addInterval((char) 0x0EA5, (char) 0x0EA5);
    namechar.addInterval((char) 0x0EA7, (char) 0x0EA7);
    namechar.addInterval((char) 0x0EAA, (char) 0x0EAB);
    namechar.addInterval((char) 0x0EAD, (char) 0x0EAE);
    namechar.addInterval((char) 0x0EB0, (char) 0x0EB0);
    namechar.addInterval((char) 0x0EB1, (char) 0x0EB1);
    namechar.addInterval((char) 0x0EB2, (char) 0x0EB3);
    namechar.addInterval((char) 0x0EB4, (char) 0x0EB9);
    namechar.addInterval((char) 0x0EBB, (char) 0x0EBC);
    namechar.addInterval((char) 0x0EBD, (char) 0x0EBD);
    namechar.addInterval((char) 0x0EC0, (char) 0x0EC4);
    namechar.addInterval((char) 0x0EC6, (char) 0x0EC6);
    namechar.addInterval((char) 0x0EC8, (char) 0x0ECD);
    namechar.addInterval((char) 0x0ED0, (char) 0x0ED9);
    namechar.addInterval((char) 0x0F18, (char) 0x0F19);
    namechar.addInterval((char) 0x0F20, (char) 0x0F29);
    namechar.addInterval((char) 0x0F35, (char) 0x0F35);
    namechar.addInterval((char) 0x0F37, (char) 0x0F37);
    namechar.addInterval((char) 0x0F39, (char) 0x0F39);
    namechar.addInterval((char) 0x0F3E, (char) 0x0F3E);
    namechar.addInterval((char) 0x0F3F, (char) 0x0F3F);
    namechar.addInterval((char) 0x0F40, (char) 0x0F47);
    namechar.addInterval((char) 0x0F49, (char) 0x0F69);
    namechar.addInterval((char) 0x0F71, (char) 0x0F84);
    namechar.addInterval((char) 0x0F86, (char) 0x0F8B);
    namechar.addInterval((char) 0x0F90, (char) 0x0F95);
    namechar.addInterval((char) 0x0F97, (char) 0x0F97);
    namechar.addInterval((char) 0x0F99, (char) 0x0FAD);
    namechar.addInterval((char) 0x0FB1, (char) 0x0FB7);
    namechar.addInterval((char) 0x0FB9, (char) 0x0FB9);
    namechar.addInterval((char) 0x10A0, (char) 0x10C5);
    namechar.addInterval((char) 0x10D0, (char) 0x10F6);
    namechar.addInterval((char) 0x1100, (char) 0x1100);
    namechar.addInterval((char) 0x1102, (char) 0x1103);
    namechar.addInterval((char) 0x1105, (char) 0x1107);
    namechar.addInterval((char) 0x1109, (char) 0x1109);
    namechar.addInterval((char) 0x110B, (char) 0x110C);
    namechar.addInterval((char) 0x110E, (char) 0x1112);
    namechar.addInterval((char) 0x113C, (char) 0x113C);
    namechar.addInterval((char) 0x113E, (char) 0x113E);
    namechar.addInterval((char) 0x1140, (char) 0x1140);
    namechar.addInterval((char) 0x114C, (char) 0x114C);
    namechar.addInterval((char) 0x114E, (char) 0x114E);
    namechar.addInterval((char) 0x1150, (char) 0x1150);
    namechar.addInterval((char) 0x1154, (char) 0x1155);
    namechar.addInterval((char) 0x1159, (char) 0x1159);
    namechar.addInterval((char) 0x115F, (char) 0x1161);
    namechar.addInterval((char) 0x1163, (char) 0x1163);
    namechar.addInterval((char) 0x1165, (char) 0x1165);
    namechar.addInterval((char) 0x1167, (char) 0x1167);
    namechar.addInterval((char) 0x1169, (char) 0x1169);
    namechar.addInterval((char) 0x116D, (char) 0x116E);
    namechar.addInterval((char) 0x1172, (char) 0x1173);
    namechar.addInterval((char) 0x1175, (char) 0x1175);
    namechar.addInterval((char) 0x119E, (char) 0x119E);
    namechar.addInterval((char) 0x11A8, (char) 0x11A8);
    namechar.addInterval((char) 0x11AB, (char) 0x11AB);
    namechar.addInterval((char) 0x11AE, (char) 0x11AF);
    namechar.addInterval((char) 0x11B7, (char) 0x11B8);
    namechar.addInterval((char) 0x11BA, (char) 0x11BA);
    namechar.addInterval((char) 0x11BC, (char) 0x11C2);
    namechar.addInterval((char) 0x11EB, (char) 0x11EB);
    namechar.addInterval((char) 0x11F0, (char) 0x11F0);
    namechar.addInterval((char) 0x11F9, (char) 0x11F9);
    namechar.addInterval((char) 0x1E00, (char) 0x1E9B);
    namechar.addInterval((char) 0x1EA0, (char) 0x1EF9);
    namechar.addInterval((char) 0x1F00, (char) 0x1F15);
    namechar.addInterval((char) 0x1F18, (char) 0x1F1D);
    namechar.addInterval((char) 0x1F20, (char) 0x1F45);
    namechar.addInterval((char) 0x1F48, (char) 0x1F4D);
    namechar.addInterval((char) 0x1F50, (char) 0x1F57);
    namechar.addInterval((char) 0x1F59, (char) 0x1F59);
    namechar.addInterval((char) 0x1F5B, (char) 0x1F5B);
    namechar.addInterval((char) 0x1F5D, (char) 0x1F5D);
    namechar.addInterval((char) 0x1F5F, (char) 0x1F7D);
    namechar.addInterval((char) 0x1F80, (char) 0x1FB4);
    namechar.addInterval((char) 0x1FB6, (char) 0x1FBC);
    namechar.addInterval((char) 0x1FBE, (char) 0x1FBE);
    namechar.addInterval((char) 0x1FC2, (char) 0x1FC4);
    namechar.addInterval((char) 0x1FC6, (char) 0x1FCC);
    namechar.addInterval((char) 0x1FD0, (char) 0x1FD3);
    namechar.addInterval((char) 0x1FD6, (char) 0x1FDB);
    namechar.addInterval((char) 0x1FE0, (char) 0x1FEC);
    namechar.addInterval((char) 0x1FF2, (char) 0x1FF4);
    namechar.addInterval((char) 0x1FF6, (char) 0x1FFC);
    namechar.addInterval((char) 0x20D0, (char) 0x20DC);
    namechar.addInterval((char) 0x20E1, (char) 0x20E1);
    namechar.addInterval((char) 0x2126, (char) 0x2126);
    namechar.addInterval((char) 0x212A, (char) 0x212B);
    namechar.addInterval((char) 0x212E, (char) 0x212E);
    namechar.addInterval((char) 0x2180, (char) 0x2182);
    namechar.addInterval((char) 0x3005, (char) 0x3005);
    namechar.addInterval((char) 0x3007, (char) 0x3007);
    namechar.addInterval((char) 0x3021, (char) 0x3029);
    namechar.addInterval((char) 0x302A, (char) 0x302F);
    namechar.addInterval((char) 0x3031, (char) 0x3035);
    namechar.addInterval((char) 0x3041, (char) 0x3094);
    namechar.addInterval((char) 0x3099, (char) 0x3099);
    namechar.addInterval((char) 0x309A, (char) 0x309A);
    namechar.addInterval((char) 0x309D, (char) 0x309E);
    namechar.addInterval((char) 0x30A1, (char) 0x30FA);
    namechar.addInterval((char) 0x30FC, (char) 0x30FE);
    namechar.addInterval((char) 0x3105, (char) 0x312C);
    namechar.addInterval((char) 0x4E00, (char) 0x9FA5);
    namechar.addInterval((char) 0xAC00, (char) 0xD7A3);
    namechar.close();
  }
}
