package net.cabezudo.sofia.core.html;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.18
 */
public class HTMLTagFactory {

  public static Tag get(String line) {
    if (!line.equals("</section>") && line.endsWith("</section>")) {
      int i = line.indexOf(">");
      if (i == -1) {
        return null;
      }
      String tag = line.substring(1, i);
      if (tag.startsWith("section")) {
        return new Section(tag.substring(7), 0);
      }
    }
    return null;
  }
}
