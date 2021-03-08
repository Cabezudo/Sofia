package net.cabezudo.sofia.core.html;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.06.18
 */
public class HTMLTagFactory {

  private HTMLTagFactory() {
    // Utility classes should not have public constructors
  }

  public static Tag get(String line) {
    int s = line.indexOf("<section");
    if (s >= 0 && line.endsWith("</section>")) {
      int e = line.indexOf('>');
      if (e == -1) {
        return null;
      }
      String tag = line.substring(1, e);
      return new Section(tag.substring(s + 8), s);
    }
    return null;
  }
}
