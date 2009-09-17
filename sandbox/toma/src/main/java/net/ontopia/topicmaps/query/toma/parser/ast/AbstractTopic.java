package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for Topic Literals in the AST.
 */
public abstract class AbstractTopic implements PathRootIF {
  public enum TYPE {
    IID, SI, SL, NAME, VAR
  };

  private TYPE idType;
  private String identifier;

  public AbstractTopic(TYPE type, String id) {
    this.idType = type;
    this.identifier = id;
  }

  public TYPE getIDType() {
    return idType;
  }

  public void setIDType(TYPE type) {
    this.idType = type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public AbstractPathElement.TYPE getType() {
    return AbstractPathElement.TYPE.TOPIC;
  }

  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(     TOPIC) [" + getIdentifier() + "]", level);
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();

    switch (idType) {
    case IID:
      sb.append("i");
      break;

    case SI:
      sb.append("si");
      break;

    case SL:
      sb.append("sl");
      break;

    case NAME:
      sb.append("n");
      break;

    case VAR:
      sb.append("v");
      break;
    }

    sb.append("'");
    sb.append(identifier);
    sb.append("'");
    return sb.toString();
  }
}
