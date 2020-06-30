package net.cabezudo.sofia.core.sic.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.28
 */
public enum TokenType {
  BOOLEAN("boolean"), CLOSE_PARENTHESES("close parentheses"), COMMA("comma"), EQUAL("equal"), FALSE("false"), NEW_LINE("new line"), NUMBER("number"), OPEN_PARENTHESES("open parentheses"), SPACE("space"), TRUE("true"), PARAMETER_NAME("parameter name"), INVALID("invalid"), PARAMETER_VALUE("parameter value"), STRING("string"), FUNCTION("function"), TABULATION("tabulation"), END_OF_CODE("end of code");
  private final String description;

  TokenType(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
