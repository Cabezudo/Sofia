package net.cabezudo.sofia.core.ws.parser.tokens;

import java.security.InvalidParameterException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.sofia.core.ws.parser.URLPathTokenizer;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class Tokens extends AbstractList<Token> {

  private final List<Token> list = new ArrayList<>();
  private final Map<String, Token> map = new TreeMap<>();

  @Override
  public boolean add(Token token) {
    return list.add(token);
  }

  @Override
  public Token get(int i) {
    return list.get(i);
  }

  public Token consume() {
    return list.remove(0);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Token t : list) {
      sb.append(t.toString());
    }
    return sb.toString();
  }

  public boolean match(String pattern) {

    Tokens patternTokens = URLPathTokenizer.tokenize(pattern);

    if (patternTokens.size() != this.size()) {
      return false;
    }

    for (int i = 0; i < patternTokens.size(); i++) {
      Token patternToken = patternTokens.get(i);
      Token pathToken = this.get(i);

      if (!patternToken.match(pathToken)) {
        return false;
      }
      if (patternToken.isVariable()) {
        map.put(patternToken.getValue(), pathToken);
      }
    }
    return true;
  }

  public Token getValue(String parameterName) {
    if (parameterName == null) {
      throw new InvalidParameterException("The parameter name is null");
    }
    Token value = map.get(parameterName);
    if (value == null) {
      throw new RuntimeException("Parameter '" + parameterName + "' not found.");
    }
    return value;
  }
}
