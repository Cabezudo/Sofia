package net.cabezudo.sofia.core.ws.parser;

import net.cabezudo.sofia.core.ws.parser.tokens.TokenFactory;
import net.cabezudo.sofia.core.ws.parser.tokens.WSToken;
import net.cabezudo.sofia.core.ws.parser.tokens.WSTokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class URLPathTokenizer {

  private URLPathTokenizer() {
    // Nothing to do here. Utility classes should not have public constructors.
  }

  public static WSTokens tokenize(String path) {
    WSTokens tokens = new WSTokens();
    int stringLength = path.length();
    StringBuilder sb = new StringBuilder();

    WSToken token = null;
    for (int i = 0; i < stringLength; i++) {
      char c = path.charAt(i);
      if (c == '/') {
        if (sb.length() > 0) {
          token = TokenFactory.get(sb.toString());
          tokens.add(token);
          sb = new StringBuilder();
        } else {
          token = TokenFactory.get("");
          tokens.add(token);
        }
        token = TokenFactory.get(c);
        tokens.add(token);
      } else {
        sb.append(c);
      }
    }
    if (sb.length() == 0) {
      if (token != null && token.isPathSeparator()) {
        tokens.add(TokenFactory.get(""));
      }
    } else {
      token = TokenFactory.get(sb.toString());
      tokens.add(token);
    }
    return tokens;
  }
}
