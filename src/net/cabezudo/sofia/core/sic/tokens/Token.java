package net.cabezudo.sofia.core.sic.tokens;

import net.cabezudo.json.JSONPair;
import net.cabezudo.json.values.JSONObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public abstract class Token {

  private final Position position;
  private final String value;
  private boolean error = false;
  private boolean invalidClass = false;

  public Token(String value, Position position) {
    this.value = value;
    this.position = position;
  }

  @Override
  public String toString() {
    return value
            + " (type: " + this.getClass().getSimpleName()
            + ", position: " + position
            + ", length: " + length()
            + ")";
  }

  public int length() {
    return value.length();
  }

  public boolean isEmpty() {
    return length() == 0;
  }

  public void setError(boolean error) {
    this.error = error;
  }

  public boolean isError() {
    return error;
  }

  public void setInvalidClass(boolean invalidClass) {
    this.invalidClass = invalidClass;
  }

  public boolean isInvalidClass() {
    return invalidClass;
  }

  public Position getPosition() {
    return position;
  }

  public String getValue() {
    return value;
  }

  public String getJSONValue() {
    return value;
  }

  public boolean isBoolean() {
    return false;
  }

  public boolean isParameterName() {
    return false;
  }

  public boolean isNewLine() {
    return false;
  }

  public boolean isNumber() {
    return false;
  }

  public boolean isComma() {
    return false;
  }

  public boolean isOpenParentheses() {
    return false;
  }

  public boolean isCloseParentheses() {
    return false;
  }

  public boolean isParameter() {
    return false;
  }

  public boolean isTabulation() {
    return false;
  }

  public boolean isFunction() {
    return false;
  }

  public boolean isEqual() {
    return false;
  }

  public boolean isSpace() {
    return false;
  }

  public JSONObject toJSON() {
    JSONObject jsonData = new JSONObject();
    JSONPair jsonValue = new JSONPair("value", getJSONValue());
    jsonData.add(jsonValue);
    JSONPair jsonClass;
    if (isInvalidClass()) {
      jsonClass = new JSONPair("class", "none");
    } else {
      jsonClass = new JSONPair("class", getCSSClass());
    }
    jsonData.add(jsonClass);
    JSONPair jsonError = new JSONPair("error", error);
    jsonData.add(jsonError);
    return jsonData;
  }

  public abstract String getCSSClass();

  public abstract TokenType getType();

  public String getDescription() {
    return getType().getDescription();
  }

  public Position getEndPosition() {
    return new Position(position.getLine(), position.getRow() + value.length());
  }
}
