package net.cabezudo.sofia.core.ws.parser;

import net.cabezudo.sofia.core.ws.parser.tokens.Token;
import net.cabezudo.sofia.core.ws.parser.tokens.TokenFactory;
import net.cabezudo.sofia.core.ws.parser.tokens.Tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class URLPathTokenizer {

  public static Tokens tokenize(String path) {
    Tokens tokens = new Tokens();
    int stringLength = path.length();
    StringBuilder sb = new StringBuilder();

    Token token = null;
    for (int i = 0; i < stringLength; i++) {
      char c = path.charAt(i);
      switch (c) {
        case '/':
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
          break;
        default:
          sb.append(c);
          break;
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
