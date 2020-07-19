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
public class WSTokens extends AbstractList<WSToken> {

  private final List<WSToken> list = new ArrayList<>();
  private final Map<String, WSToken> map = new TreeMap<>();

  @Override
  public boolean add(WSToken token) {
    return list.add(token);
  }

  @Override
  public WSToken get(int i) {
    return list.get(i);
  }

  public WSToken consume() {
    return list.remove(0);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (WSToken t : list) {
      sb.append(t.getClass().getSimpleName()).append(" ").append(t.toString()).append('\n');
    }
    return sb.toString();
  }

  public boolean match(String pattern) {
    WSTokens patternTokens = URLPathTokenizer.tokenize(pattern);

    if (patternTokens.size() != this.size()) {
      return false;
    }

    for (int i = 0; i < patternTokens.size(); i++) {
      WSToken patternToken = patternTokens.get(i);
      WSToken pathToken = this.get(i);

      if (!patternToken.match(pathToken)) {
        return false;
      }
      if (patternToken.isVariable()) {
        map.put(patternToken.toString(), pathToken);
      }
    }
    return true;
  }

  public WSToken getValue(String parameterName) {
    if (parameterName == null) {
      throw new InvalidParameterException("The parameter name is null");
    }
    WSToken value = map.get(parameterName);
    if (value == null) {
      throw new RuntimeException("Parameter '" + parameterName + "' not found.");
    }
    return value;
  }
}
