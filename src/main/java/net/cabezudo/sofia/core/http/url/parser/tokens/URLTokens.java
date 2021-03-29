package net.cabezudo.sofia.core.http.url.parser.tokens;

import java.security.InvalidParameterException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.cabezudo.sofia.core.exceptions.SofiaRuntimeException;
import net.cabezudo.sofia.core.http.url.parser.URLPathTokenizer;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2018.07.15
 */
public class URLTokens extends AbstractList<URLToken> {

  private final List<URLToken> list = new ArrayList<>();
  private final Map<String, URLToken> map = new TreeMap<>();

  @Override
  public boolean add(URLToken token) {
    return list.add(token);
  }

  @Override
  public URLToken get(int i) {
    return list.get(i);
  }

  public URLToken consume() {
    return list.remove(0);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (URLToken t : list) {
      sb.append(t.toString());
    }
    return sb.toString();
  }

  public boolean match(String pattern) {
    URLTokens patternTokens = URLPathTokenizer.tokenize(pattern);

    if (patternTokens.size() != this.size()) {
      return false;
    }

    for (int i = 0; i < patternTokens.size(); i++) {
      URLToken patternToken = patternTokens.get(i);
      URLToken pathToken = this.get(i);

      if (!patternToken.match(pathToken)) {
        return false;
      }
      if (patternToken.isVariable()) {
        map.put(patternToken.toString(), pathToken);
      }
    }
    return true;
  }

  public URLToken getValue(String parameterName) {
    if (parameterName == null) {
      throw new InvalidParameterException("The parameter name is null");
    }
    URLToken value = map.get(parameterName);
    if (value == null) {
      throw new SofiaRuntimeException("Parameter '" + parameterName + "' not found.");
    }
    return value;
  }
}
