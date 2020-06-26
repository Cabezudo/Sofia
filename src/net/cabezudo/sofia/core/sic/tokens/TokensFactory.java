package net.cabezudo.sofia.core.sic.tokens;

import java.math.BigDecimal;
import net.cabezudo.sofia.core.sic.StringToken;
import net.cabezudo.sofia.core.sic.exceptions.UnexpectedTokenException;
import net.cabezudo.sofia.core.sic.tokens.functions.CreateImageFunctionToken;
import net.cabezudo.sofia.core.sic.tokens.functions.MainFunctionToken;
import net.cabezudo.sofia.core.sic.tokens.functions.ResizeFunctionToken;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class TokensFactory {

  public static Token get(StringBuilder sb, Position position) throws UnexpectedTokenException {
    return get(sb.toString(), position);
  }

  public static Token get(char c, Position position) throws UnexpectedTokenException {
    return get(Character.toString(c), position);
  }

  public static Token get(String s, Position position) throws UnexpectedTokenException {
    switch (s.length()) {
      case 0:
        throw new RuntimeException("Zero length token.");
      case 1:
        switch (s) {
          case "\n":
            return new NewLineToken(position);
          case " ":
          case "\u00A0":
            return new SpaceToken(position);
          case ",":
            return new CommaToken(position);
          case "=":
            return new EqualToken(position);
          case "(":
            return new OpenParenthesesToken(position);
          case ")":
            return new CloseParenthesesToken(position);
        }
        break;
      case 2:
        switch (s) {
          case "\"\"":
            return new StringToken("", position);
        }
        break;
    }
    switch (s.toLowerCase()) {
      case "true":
        return new TrueToken(position);
      case "false":
        return new FalseToken(position);
      default:
        do {
          if (s.startsWith("\"") && s.endsWith("\"")) {
            return new StringToken(s.substring(1, s.length() - 1), position);
          }
          try {
            BigDecimal number = new BigDecimal(s);
            return new NumberToken(number.toString(), position);
          } catch (NumberFormatException e) {
            switch (s) {
              case "main":
                return new MainFunctionToken(position);
              case "loadImage":
                return new CreateImageFunctionToken(position);
              case "resize":
                return new ResizeFunctionToken(position);
              case "name":
              case "width":
              case "height":
              case "scale":
                return new ParameterNameToken(s, position);
              default:
                return new ParameterValueToken(s, position);
            }
          }
        } while (false);
    }
  }
}