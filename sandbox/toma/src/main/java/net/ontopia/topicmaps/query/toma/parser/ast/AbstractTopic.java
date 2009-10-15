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
    buf.append("(     TOPIC) [" + getTypeString() + "] [" + getIdentifier() + "]", level);
  }

  private String getTypeString() {
    switch (idType) {
    case IID:
      return "i";

    case SI:
      return "si";

    case SL:
      return "sl";

    case NAME:
      return "n";

    case VAR:
      return "v";
    }
    
    return "unknown";
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getTypeString());
    sb.append("'");
    sb.append(identifier);
    sb.append("'");
    return sb.toString();
  }
}
