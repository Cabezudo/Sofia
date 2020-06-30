package net.cabezudo.sofia.core.sic;

import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;
import net.cabezudo.sofia.core.sic.tokens.TokensFactory;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class Tokenizer {

  @SuppressWarnings("fallthrough")
  public static Tokens tokenize(String code, SICCompilerMessages messages) {
    char[] chars = code.toCharArray();
    Tokens tokens = new Tokens();
    boolean isString = false;
    StringBuilder sb = new StringBuilder();

    int line = 1;
    int row = 1;
    Position position = new Position(line, row);

    for (int i = 0; i < chars.length; i++, row++) {
      char c = chars[i];
      if (isString) {
        do {
          if (c == '\\') {
            sb.append(c);
            i++;
            if (i < chars.length) {
              c = chars[i];
              sb.append(c);
            }
            break;
          }
          if (c == '"') {
            sb.append(c);
            isString = false;
            Token token = TokensFactory.get(sb, position);
            position = new Position(line, row);
            tokens.add(token);
            sb = new StringBuilder();
          } else {
            sb.append(c);
          }
        } while (false);
      } else {
        switch (c) {
          case '"':
            sb.append(c);
            isString = true;
            break;
          case '\n':
            line++;
            row = 1;
          case ' ':
          case '\u00A0':
          case '\t':
          case ',':
          case '(':
          case ')':
          case '=':
            if (sb.length() > 0) {
              Token token = TokensFactory.get(sb, position);
              tokens.add(token);
              position = new Position(line, row);
            }
            Token token = TokensFactory.get(c, position);
            tokens.add(token);
            position = new Position(line, row + 1);
            sb = new StringBuilder();
            break;
          default:
            sb.append(c);
            break;
        }
      }
    }
    if (sb.length() > 0) {
      Token token = TokensFactory.get(sb, position);
      tokens.add(token);
    }
    return tokens;
  }

  private Tokenizer() {
  }
}
