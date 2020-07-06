package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public abstract class SICParameter extends SICParameterOrFunction {

  private final Token name;
  private final Token value;

  public SICParameter(Token name, Token value) {
    super(name.getValue(), name);
    this.name = name;
    this.value = value;
  }

  @Override
  public String toString(int deep) {
    return Utils.repeat(' ', deep * 2) + name.getValue() + "=" + value.getValue();
  }

  public boolean isFactorParameter() {
    return false;
  }

  public boolean isHeightParameter() {
    return false;
  }

  public boolean isNameParameter() {
    return false;
  }

  public boolean isScaleParameter() {
    return false;
  }

  public boolean isValueParameter() {
    return false;
  }

  public Token getNameToken() {
    return name;
  }

  public Token getValueToken() {
    return value;
  }

  public boolean isWidthParameter() {
    return false;
  }
}
