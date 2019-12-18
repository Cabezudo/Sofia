package net.cabezudo.sofia.core.creator;

import java.sql.SQLException;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.12.18
 */
interface SofiaSource {

  void add(Line line);

  void add(Lines lines);

  Lines getLines();

  Lines getJavaScriptLines();

  Lines getCascadingStyleSheetLines();

  String getVoidPartialPathName();

  SofiaSource searchHTMLTag(SofiaSource actual, String line, int lineNumber) throws SQLException, InvalidFragmentTag;

}
