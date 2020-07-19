package net.cabezudo.sofia.core.html;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.18
 */
public class Section extends Tag {

  public Section(String data, int row) {
    super("section", data, row);
  }

  @Override
  public boolean isSection() {
    return true;
  }

}
