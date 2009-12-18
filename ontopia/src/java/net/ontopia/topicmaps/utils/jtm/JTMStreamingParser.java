/*
 * Copyright 2009 Thomas Neidhart, thomas.neidhart@spaceapplications.com
 *   based on work from Lars Heuer (heuer[at]semagia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ontopia.topicmaps.utils.jtm;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;

import net.ontopia.infoset.core.LocatorIF;
import net.ontopia.topicmaps.core.AssociationIF;
import net.ontopia.topicmaps.core.AssociationRoleIF;
import net.ontopia.topicmaps.core.OccurrenceIF;
import net.ontopia.topicmaps.core.ReifiableIF;
import net.ontopia.topicmaps.core.ScopedIF;
import net.ontopia.topicmaps.core.TMObjectIF;
import net.ontopia.topicmaps.core.TopicIF;
import net.ontopia.topicmaps.core.TopicMapBuilderIF;
import net.ontopia.topicmaps.core.TopicMapIF;
import net.ontopia.topicmaps.core.TopicNameIF;
import net.ontopia.topicmaps.core.VariantNameIF;
import net.ontopia.topicmaps.utils.MergeUtils;
import net.ontopia.topicmaps.utils.PSI;

/**
 * INTERNAL: A streaming JTM parser.
 * 
 * The JTM parser supports the top level item types "topicmap", "topic",
 * "association", "occurrence" and "name". Detached roles and variants are not
 * supported.
 */
final class JTMStreamingParser {
  /**
   * Supported version of the JTM notation is currently "1.0"
   */
  public final static String VERSION = "1.0";

  private TopicMapIF tm;
  private TopicMapBuilderIF builder;
  private LocatorIF baseURI;

  /**
   * Create a new parser that will store the results into the given topic map.
   * 
   * @param topicmap the topic map to import into.
   */
  public JTMStreamingParser(TopicMapIF topicmap) {
    this.tm = topicmap;
    this.builder = tm.getBuilder();
    this.baseURI = topicmap.getStore().getBaseAddress();
  }

  /**
   * INTERNAL: Parses a topic map in JTM 1.0 notation from the input reader.
   * 
   * @param reader the input to read the topic map from.
   * @throws IOException if some error occurs while reading from the input.
   * @throws JTMException if the topic map to be parsed is not in JTM 1.0
   *           syntax.
   */
  public void parse(final Reader reader) throws IOException, JTMException {
    JSONParser parser = new JSONParser(reader);

    if (parser.nextToken() != JSONToken.START_OBJECT) {
      throw new JTMException("Expected input to start with an object: '"
          + JSONToken.nameOf(JSONToken.START_OBJECT) + "'.");
    }

    if (parser.nextToken() != JSONToken.KW_VERSION) {
      throw new JTMException("Expected 'version' at the beginning.");
    }
    parser.nextToken();
    if (!VERSION.equals(parser.getText())) {
      throw new JTMException("Unsupported version: '" + parser.getText() + "'.");
    }

    if (parser.nextToken() != JSONToken.KW_ITEM_TYPE) {
      throw new JTMException("Expected 'item_type' after the version.");
    }

    handleItemType(parser);
  }

  /**
   * INTERNAL: Based on the current item type of the input, call the appropriate
   * handle method.
   */
  private void handleItemType(final JSONParser parser) throws IOException,
      JTMException {
    parser.nextToken();
    // The item type is case-insensitive; force lower case.
    final String itemType = parser.getText().toLowerCase();
    if ("topicmap".equals(itemType)) {
      handleTopicMap(parser);
    } else if ("topic".equals(itemType)) {
      handleTopic(parser);
    } else if ("association".equals(itemType)) {
      handleAssociation(parser);
    } else if ("occurrence".equals(itemType)) {
      handleOccurrence(parser, null);
    } else if ("name".equals(itemType)) {
      handleName(parser, null);
    } else if ("role".equals(itemType)) {
      throw new JTMException("Detached roles are not supported.");
    } else if ("variant".equals(itemType)) {
      throw new JTMException("Detached variants are not supported.");
    } else {
      throw new JTMException("Unknown item type: " + itemType);
    }
  }

  /**
   * INTERNAL: Handle jtm object of type topic map.
   */
  private void handleTopicMap(final JSONParser parser) throws IOException,
      JTMException {
    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_IIDS:
        Collection<LocatorIF> iids = handleItemIdentifiers(parser);
        if (iids != null) {
          for (LocatorIF iid : iids) {
            TMObjectIF obj = tm.getObjectByItemIdentifier(iid);
            if (obj != null) {
              throw new JTMException("Item Identifier for topic map already "
                  + "used by another construct.");
            } else {
              tm.addItemIdentifier(iid);
            }
          }
        }
        break;
      case JSONToken.KW_REIFIER:
        setReifier(tm, handleReifier(parser));
        break;
      case JSONToken.KW_TOPICS:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            handleTopic(parser);
          }
          break;
        }
      case JSONToken.KW_ASSOCIATIONS:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            handleAssociation(parser);
          }
          break;
        }
      default:
        reportIllegalField(parser);
      }
    }
  }

  /**
   * INTERNAL: Handle jtm object of type topic.
   */
  private void handleTopic(final JSONParser parser) throws IOException,
      JTMException {
    boolean seenIdentity = false;
    TopicIF topic = tm.getBuilder().makeTopic();

    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_SIDS:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            LocatorIF sid = resolveIRI(parser.getText());
            TopicIF existingTopic = tm.getTopicBySubjectIdentifier(sid);
            if (existingTopic != null) {
              if (existingTopic != topic) {
                MergeUtils.mergeInto(topic, existingTopic);
              }
            } else {
              topic.addSubjectIdentifier(sid);
            }
            seenIdentity = true;
          }
          break;
        }
      case JSONToken.KW_SLOS:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            LocatorIF slo = resolveIRI(parser.getText());
            TopicIF existingTopic = tm.getTopicBySubjectLocator(slo);
            if (existingTopic != null) {
              if (topic != existingTopic) {
                MergeUtils.mergeInto(topic, existingTopic);
              }
            } else {
              topic.addSubjectLocator(slo);
            }
            seenIdentity = true;
          }
          break;
        }
      case JSONToken.KW_IIDS:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            LocatorIF iid = resolveIRI(parser.getText());
            TopicIF existingTopic = (TopicIF) tm.getObjectByItemIdentifier(iid);
            if (existingTopic != null) {
              if (topic != existingTopic) {
                MergeUtils.mergeInto(topic, existingTopic);
              }
            } else {
              topic.addItemIdentifier(iid);
            }
            seenIdentity = true;
          }
          break;
        }
      case JSONToken.KW_OCCURRENCES:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          if (!seenIdentity) {
            throw new JTMException(
                "Cannot process occurrences without a previously read identity");
          }
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            handleOccurrence(parser, topic);
          }
          break;
        }
      case JSONToken.KW_NAMES:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          if (!seenIdentity) {
            throw new JTMException(
                "Cannot process names without a previously read identity");
          }
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            handleName(parser, topic);
          }
          break;
        }
      default:
        reportIllegalField(parser);
      }
    }

    if (!seenIdentity) {
      throw new JTMException("The topic has no identity.");
    }
  }

  /**
   * INTERNAL: Handle jtm object of type occurrence.
   */
  private void handleOccurrence(final JSONParser parser, TopicIF topic)
      throws IOException, JTMException {
    boolean seenType = false;
    LocatorIF datatype = PSI.getXSDString();
    String value = null;
    TopicIF type = null;
    Collection<LocatorIF> iids = null;
    Collection<TopicIF> scopes = null;
    TopicIF reifier = null;
    TopicIF parent = null;

    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_TYPE:
        if (!seenType) {
          parser.nextToken();
          type = makeTopicRef(parser.getText());
          seenType = true;
          break;
        }
      case JSONToken.KW_VALUE:
        if (value == null) {
          parser.nextToken();
          value = parser.getText();
          break;
        }
      case JSONToken.KW_DATATYPE:
        parser.nextToken();
        datatype = resolveIRI(parser.getText());
        break;
      case JSONToken.KW_IIDS:
        iids = handleItemIdentifiers(parser);
        break;
      case JSONToken.KW_REIFIER:
        reifier = handleReifier(parser);
        break;
      case JSONToken.KW_SCOPE:
        scopes = handleScope(parser);
        break;
      case JSONToken.KW_PARENT:
        parent = getParentTopic(parser);
        break;
      default:
        reportIllegalField(parser);
      }
    }
    if (value == null) {
      throw new JTMException("The value of the occurrence is undefined.");
    }
    if (!seenType) {
      throw new JTMException("The type of the occurrence is undefined.");
    }
    if (topic == null) {
      if (parent == null) {
        throw new JTMException("The parent of the occurrence is undefined.");
      } else {
        topic = parent;
      }
    }

    OccurrenceIF oc;
    if (datatype.equals(PSI.getXSDURI())) {
      oc = tm.getBuilder().makeOccurrence(topic, type, resolveIRI(value));
    } else {
      oc = tm.getBuilder().makeOccurrence(topic, type, value, datatype);
    }

    setScopes(oc, scopes);
    setReifier(oc, reifier);
    setItemIdentifiers(oc, iids);
  }

  /**
   * INTERNAL: Handle jtm object of type name.
   */
  private void handleName(final JSONParser parser, TopicIF topic)
      throws IOException, JTMException {
    boolean seenValue = false;
    boolean seenType = false;
    boolean seenParent = false;
    boolean requireParent = false;
    TopicIF type = null;
    String value = null;
    Collection<LocatorIF> iids = null;
    Collection<TopicIF> scopes = null;
    TopicIF reifier = null;
    TopicIF parent = null;

    // Create an empty name object.
    if (topic == null) {
      topic = tm.getBuilder().makeTopic();
      requireParent = true;
    }
    TopicNameIF name = tm.getBuilder().makeTopicName(topic, "");

    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_TYPE:
        if (!seenType) {
          parser.nextToken();
          type = makeTopicRef(parser.getText());
          seenType = true;
          break;
        }
      case JSONToken.KW_VALUE:
        if (!seenValue) {
          parser.nextToken();
          value = parser.getText();
          seenValue = true;
          break;
        }
      case JSONToken.KW_IIDS:
        iids = handleItemIdentifiers(parser);
        break;
      case JSONToken.KW_REIFIER:
        reifier = handleReifier(parser);
        break;
      case JSONToken.KW_SCOPE:
        scopes = handleScope(parser);
        break;
      case JSONToken.KW_VARIANTS:
        if (parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            handleVariant(parser, name);
          }
          break;
        }
      case JSONToken.KW_PARENT:
        parent = getParentTopic(parser);
        seenParent = (parent != null) ? true : false;
        break;
      default:
        reportIllegalField(parser);
      }
    }

    if (!seenValue) {
      throw new JTMException("The value of the name is undefined");
    }
    if (!seenType) {
      type = makeTopicRef("si:" + PSI.SAM_NAMETYPE);
    }
    if (requireParent) {
      if (!seenParent) {
        throw new JTMException("The parent of the occurrence is undefined.");
      } else {
        MergeUtils.mergeInto(topic, parent);
      }
    }

    name.setValue(value);
    name.setType(type);

    setScopes(name, scopes);
    setReifier(name, reifier);
    setItemIdentifiers(name, iids);
  }

  /**
   * INTERNAL: Handle jtm object of type variant.
   */
  private void handleVariant(final JSONParser parser, TopicNameIF name)
      throws IOException, JTMException {
    boolean seenScope = false;
    LocatorIF datatype = PSI.getXSDString();
    String value = null;
    Collection<LocatorIF> iids = null;
    Collection<TopicIF> scopes = null;
    TopicIF reifier = null;

    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_VALUE:
        if (value == null) {
          parser.nextToken();
          value = parser.getText();
          break;
        }
      case JSONToken.KW_DATATYPE:
        parser.nextToken();
        datatype = resolveIRI(parser.getText());
        break;
      case JSONToken.KW_IIDS:
        iids = handleItemIdentifiers(parser);
        break;
      case JSONToken.KW_REIFIER:
        reifier = handleReifier(parser);
        break;
      case JSONToken.KW_SCOPE:
        if (!seenScope) {
          scopes = handleScope(parser);
          seenScope = true;
          break;
        }
      default:
        reportIllegalField(parser);
      }
    }
    if (!seenScope) {
      throw new JTMException("The scope of the variant is undefined.");
    }
    if (value == null) {
      throw new JTMException("The value of the variant is undefined.");
    }

    VariantNameIF variant;
    if (datatype.equals(PSI.getXSDURI())) {
      variant = tm.getBuilder()
          .makeVariantName(name, resolveIRI(value), scopes);
    } else {
      variant = tm.getBuilder().makeVariantName(name, value, datatype, scopes);
    }

    setReifier(variant, reifier);
    setItemIdentifiers(variant, iids);
  }

  /**
   * INTERNAL: Handle jtm object of type association.
   */
  private void handleAssociation(final JSONParser parser) throws IOException,
      JTMException {
    boolean seenType = false;
    boolean seenRoles = false;
    TopicIF type = null;
    Collection<LocatorIF> iids = null;
    Collection<TopicIF> scopes = null;
    TopicIF reifier = null;

    // create an empty type in advance, the real type will be set later
    TopicIF emptyType = tm.getBuilder().makeTopic();
    AssociationIF assoc = tm.getBuilder().makeAssociation(emptyType);

    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_TYPE:
        if (!seenType) {
          parser.nextToken();
          type = makeTopicRef(parser.getText());
          seenType = true;
          break;
        }
      case JSONToken.KW_IIDS:
        iids = handleItemIdentifiers(parser);
        break;
      case JSONToken.KW_REIFIER:
        reifier = handleReifier(parser);
        break;
      case JSONToken.KW_SCOPE:
        scopes = handleScope(parser);
        break;
      case JSONToken.KW_ROLES:
        if (!seenRoles && parser.nextToken() == JSONToken.START_ARRAY) {
          while (parser.nextToken() != JSONToken.END_ARRAY) {
            handleRole(parser, assoc);
          }
          seenRoles = true;
          break;
        }
      default:
        reportIllegalField(parser);
      }
    }
    if (!seenType) {
      throw new JTMException("The type of the association is undefined");
    }
    if (!seenRoles) {
      throw new JTMException("The association has no roles");
    }

    assoc.setType(type);
    // remove the temporarily created type
    emptyType.remove();

    setScopes(assoc, scopes);
    setReifier(assoc, reifier);
    setItemIdentifiers(assoc, iids);
  }

  /**
   * INTERNAL: Handle jtm object of type role.
   */
  private void handleRole(final JSONParser parser, AssociationIF assoc)
      throws IOException, JTMException {
    boolean seenType = false;
    boolean seenPlayer = false;
    TopicIF type = null;
    TopicIF player = null;
    Collection<LocatorIF> iids = null;
    TopicIF reifier = null;

    while (parser.nextToken() != JSONToken.END_OBJECT) {
      switch (parser.getCurrentToken()) {
      case JSONToken.KW_TYPE:
        if (!seenType) {
          parser.nextToken();
          type = makeTopicRef(parser.getText());
          seenType = true;
          break;
        }
      case JSONToken.KW_PLAYER:
        if (!seenPlayer) {
          parser.nextToken();
          player = makeTopicRef(parser.getText());
          seenPlayer = true;
          break;
        }
      case JSONToken.KW_IIDS:
        iids = handleItemIdentifiers(parser);
        break;
      case JSONToken.KW_REIFIER:
        reifier = handleReifier(parser);
        break;
      case JSONToken.KW_PARENT:
        // ignore parent key, in case it is present.
        break;
      default:
        reportIllegalField(parser);
      }
    }
    if (!seenType) {
      throw new JTMException("The type of the role is undefined.");
    }
    if (!seenPlayer) {
      throw new JTMException("The player of the role is undefined.");
    }

    AssociationRoleIF role = tm.getBuilder().makeAssociationRole(assoc, type,
        player);

    setReifier(role, reifier);
    setItemIdentifiers(role, iids);
  }

  /**
   * INTERNAL: Returns a {@link TopicIF} object that is referenced bu a JTM
   * reference string.
   * 
   * @param tid A string which starts with 'si:', 'sl:' or 'ii:' followed by an
   *          IRI reference.
   */
  private TopicIF makeTopicRef(final String tid) throws JTMException {
    char[] chars = tid.toCharArray();
    if (chars.length > 3 && chars[2] == ':') {
      LocatorIF iri = resolveIRI(new String(chars, 3, chars.length - 3));
      if (chars[0] == 's') {
        if (chars[1] == 'i') {
          TopicIF topic = tm.getTopicBySubjectIdentifier(iri);
          if (topic == null) {
            topic = builder.makeTopic();
            topic.addSubjectIdentifier(iri);
          }
          return topic;
        } else if (chars[1] == 'l') {
          TopicIF topic = tm.getTopicBySubjectLocator(iri);
          if (topic == null) {
            topic = builder.makeTopic();
            topic.addSubjectLocator(iri);
          }
          return topic;
        }
      } else if (chars[0] == 'i' && chars[1] == 'i') {
        TopicIF topic = (TopicIF) tm.getObjectByItemIdentifier(iri);
        if (topic == null) {
          topic = builder.makeTopic();
          topic.addItemIdentifier(iri);
        }
        return topic;
      }
    }
    throw new JTMException("Unknown topic reference: " + tid);
  }

  /**
   * INTERNAL: Returns a {@link LocatorIF} of the given iri, that is resolved to
   * the base IRI of the topic map.
   */
  private LocatorIF resolveIRI(final String iri) {
    return baseURI.resolveAbsolute(iri);
  }

  /**
   * INTERNAL: Returns the reifier iff it is not <tt>null</tt>.
   */
  private TopicIF handleReifier(final JSONParser parser) throws IOException,
      JTMException {
    TopicIF reifier = null;
    if (parser.nextToken() != JSONToken.VALUE_NULL) {
      reifier = makeTopicRef(parser.getText());
    }
    return reifier;
  }

  /**
   * INTERNAL: Returns the parent topic for detached jtm objects (e.g. name,
   * occurrence).
   */
  private TopicIF getParentTopic(final JSONParser parser) throws IOException,
      JTMException {
    if (parser.nextToken() != JSONToken.START_ARRAY) {
      throw new JTMException("Expected an array for the parent value.");
    }
    TopicIF topic = null;
    // iterate over all elements of the array, and merge the resulting topics
    // together in a single one.
    while (parser.nextToken() != JSONToken.END_ARRAY) {
      TopicIF tmp = makeTopicRef(parser.getText());
      if (topic == null) {
        topic = tmp;
      } else {
        MergeUtils.mergeInto(topic, tmp);
      }
    }
    return topic;
  }

  /**
   * INTERNAL: Returns a collection of item identifiers that are available for
   * the current jtm object.
   */
  private Collection<LocatorIF> handleItemIdentifiers(final JSONParser parser)
      throws IOException, JTMException {
    if (parser.nextToken() != JSONToken.START_ARRAY) {
      throw new JTMException("Expected an array for the item identifiers");
    }
    Collection<LocatorIF> iids = new LinkedList<LocatorIF>();
    while (parser.nextToken() != JSONToken.END_ARRAY) {
      iids.add(resolveIRI(parser.getText()));
    }
    return iids;
  }

  /**
   * INTERNAL: Returns all the themes that are associated with the current jtm
   * object. The object itself has to check whether it allows scopes or not.
   */
  private Collection<TopicIF> handleScope(final JSONParser parser)
      throws IOException, JTMException {
    if (parser.nextToken() != JSONToken.START_ARRAY) {
      throw new JTMException("Expected an array for the scope themes.");
    }
    Collection<TopicIF> scopes = new LinkedList<TopicIF>();
    while (parser.nextToken() != JSONToken.END_ARRAY) {
      scopes.add(makeTopicRef(parser.getText()));
    }
    return scopes;
  }

  /**
   * INTERNAL: Set the reifier for a reifiable topic map construct. If the
   * reifier already reifies another construct, ignore it. Duplicate constructs
   * will be handled later.
   */
  private void setReifier(ReifiableIF object, TopicIF reifier)
      throws JTMException {
    if (reifier != null) {
      if (reifier.getReified() == null) {
        object.setReifier(reifier);
      } else {
        // TMObjectIF other = reifier.getReified();
        // // if they are of the same class, try to merge them
        // if (object.getClass().isAssignableFrom(other.getClass())) {
        // MergeUtils.mergeInto(object, (ReifiableIF) other);
        // }
      }
    }
  }

  /**
   * INTERNAL: Assigns the collection of item identifiers to a topic map
   * construct. This method checks whether an item identifier is already in use.
   */
  private void setItemIdentifiers(TMObjectIF object, Collection<LocatorIF> iids) {
    if (iids != null) {
      for (LocatorIF iid : iids) {
        TMObjectIF obj = tm.getObjectByItemIdentifier(iid);
        if (obj != null) {
        } else {
          object.addItemIdentifier(iid);
        }
      }
    }
  }

  /**
   * INTERNAL: Assign the given scopes to the specified scoped topic map
   * construct.
   */
  private void setScopes(ScopedIF object, Collection<TopicIF> scopes) {
    if (scopes != null) {
      for (TopicIF scope : scopes) {
        object.addTheme(scope);
      }
    }
  }

  private void reportIllegalField(final JSONParser parser) throws IOException,
      JTMException {
    throw new JTMException("Unknown key name: '" + parser.getText()
        + "' current: " + JSONToken.nameOf(parser.getCurrentToken()));
  }
}
