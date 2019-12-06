package net.cabezudo.sofia.core.creator;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.11.12
 */
public class SofiaHTMLSource extends SofiaSource {

  SofiaHTMLSource(Caller caller) {
    super(caller);
  }

  @Override
  Type getType() {
    return Type.HTML;
  }

  @Override
  String getDescription() {
    return super.getPartialPath().toString();
  }
}
