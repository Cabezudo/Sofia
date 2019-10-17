package net.cabezudo.sofia.core.http;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author estebancabezudo
 */
public class QueryString {

  private final Map<String, List<String>> query;

  public QueryString(HttpServletRequest request) {
    String queryString = request.getQueryString();

    if (queryString == null || queryString.isBlank()) {
      query = Collections.emptyMap();
    } else {
      query = Arrays
          .stream(queryString.split("&"))
          .map(this::splitQueryParameter)
          .collect(Collectors.groupingBy(SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }
  }

  private SimpleImmutableEntry<String, String> splitQueryParameter(String pair) {
    final int i = pair.indexOf("=");
    final String key = i > 0 ? pair.substring(0, i) : pair;
    final String value = i > 0 && pair.length() > i + 1 ? pair.substring(i + 1) : null;
    return new SimpleImmutableEntry<>(key, value);
  }

  public List<String> get(String key) {
    return query.get(key);
  }
}
