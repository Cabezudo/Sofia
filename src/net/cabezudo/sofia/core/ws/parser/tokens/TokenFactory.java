package net.cabezudo.sofia.core.ws.parser.tokens;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class TokenFactory {

  public static Token get(char c) {
    return get(Character.toString(c));
  }

  public static Token get(String s) {
    if ("/".equals(s)) {
      return new PathSeparatorToken();
    }
    try {
      Integer.parseInt(s);
      return new NumberToken(s);
    } catch (NumberFormatException e) {
      if (s.startsWith("{") && s.endsWith("}")) {
        return new VariableToken(s);
      }
      return new StringToken(s);
    }
  }
}
