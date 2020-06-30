package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public abstract class SICParameterOrFunction extends SICElement {

  private final String name;

  public SICParameterOrFunction(String name, Position position) {
    super(position);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean isParameter() {
    return this instanceof SICParameter;
  }

  @Override
  public boolean isFunction() {
    return this instanceof SICFunction;
  }
}
