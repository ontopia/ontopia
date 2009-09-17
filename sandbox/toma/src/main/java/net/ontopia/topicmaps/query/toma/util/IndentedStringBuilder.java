package net.ontopia.topicmaps.query.toma.util;

/**
 * PUBLIC: An indented StringBuilder implementation. This class can be used to
 * create indented tree-like outputs e.g. a syntax tree.
 */
public class IndentedStringBuilder {
  public static final int DEFAULT_INDENTATION_SIZE = 2;

  private StringBuilder internalBuffer;
  private int indentSize;
  private int cnt;

  /**
   * PUBLIC: Create a new indented StringBuilder instance with a default
   * indentation of {@link DEFAULT_INDENTATION_SIZE}.
   */
  public IndentedStringBuilder() {
    this(DEFAULT_INDENTATION_SIZE);
  }

  /**
   * PUBLIC: Create a new indented StringBuilder instance with a custom
   * indentation size.
   * 
   * @param indentSize the desired indentation size.
   */
  public IndentedStringBuilder(int indentSize) {
    internalBuffer = new StringBuilder();
    this.indentSize = indentSize;
    this.cnt = 1;
  }

  /**
   * PUBLIC: Append a line to the current buffer. 
   * Note: a carriage return is automatically added to the line.
   * 
   * @param line the line to be added.
   * @param level the required level of indentation.
   */
  public void append(String line, int level) {
    addIndentation(level);

    internalBuffer.append(String.format("==%1$2s== ", cnt++));
    internalBuffer.append(line);
    internalBuffer.append('\n');
  }

  /**
   * PUBLIC: Get a string representation.
   * 
   * @return a string representation
   */
  @Override
  public String toString() {
    return internalBuffer.toString();
  }

  /**
   * INTERNAL: Add the required indentation.
   * 
   * @param level the required indentation level.
   */
  private void addIndentation(int level) {
    String indentation = "";
    if (level > 0 && indentSize > 0) {
      indentation = String.format("%1$" + level * indentSize + "s", ' ');
    }
    internalBuffer.append(indentation);
  }
}
