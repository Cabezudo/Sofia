package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public abstract class InlineFile extends Line {

  private final SofiaSource source;

  public InlineFile(SofiaSource source, int lineNumber) {
    super(lineNumber);
    this.source = source;
  }
}
