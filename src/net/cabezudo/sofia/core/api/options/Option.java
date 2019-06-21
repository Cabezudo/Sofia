package net.cabezudo.sofia.core.api.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2019.03.14
 */
public abstract class Option {

  public static final String FILTERS = "filters";
  public static final String SORT = "sort";
  public static final String FIELDS = "fields";
  public static final String OFFSET = "offset";
  public static final String LIMIT = "limit";
  private final String name;
  private final List<OptionValue> values = new ArrayList<>();
  private final Integer value;
  private final String originalValue;

  public Option(String name, Integer value) {
    this.name = name;
    this.value = value;
    this.originalValue = null;
  }

  public Option(String name, String value) {
    this.name = name;
    this.value = null;
    this.originalValue = value;
  }

  public String getName() {
    return name;
  }

  protected void processMultipleStrings() {
    if (originalValue == null || originalValue.isEmpty()) {
      return;
    }
    String[] vs = originalValue.replaceAll("\\s", ",").split(",");
    for (String v : vs) {
      OptionValue optionValue = createOptionValue(v);
      values.add(optionValue);
    }
  }

  protected abstract OptionValue createOptionValue(String v);

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[ name = ");
    sb.append(name);
    sb.append(", values = ");
    sb.append(values);
    sb.append(" ]");
    return sb.toString();
  }

  public long getValue() {
    return value;
  }

  public List<OptionValue> getValues() {
    return Collections.unmodifiableList(values);
  }

  public String getOriginalValue() {
    return originalValue;
  }
}
