package net.ontopia.topicmaps.query.toma.parser.ast;

import net.ontopia.topicmaps.query.toma.util.IndentedStringBuilder;

/**
 * INTERNAL: Abstract base class for Topic Literals in the AST.
 */
public abstract class AbstractTopic extends AbstractPathElement {
  public enum IDTYPE {
    IID, SI, SL, NAME, VAR
  };

  private IDTYPE idType;
  private String identifier;

  public AbstractTopic(IDTYPE type, String id) {
    super("TOPIC");
    this.idType = type;
    this.identifier = id;
  }

  public IDTYPE getIDType() {
    return idType;
  }

  public void setIDType(IDTYPE type) {
    this.idType = type;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public void fillParseTree(IndentedStringBuilder buf, int level) {
    buf.append("(     TOPIC) [" + getIdentifier() + "]", level);
  }

  @Override
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
