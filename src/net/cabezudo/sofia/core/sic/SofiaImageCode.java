package net.cabezudo.sofia.core.sic;

import java.nio.file.Path;
import net.cabezudo.json.values.JSONArray;
import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sic.elements.SICCompileTimeException;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFactory;
import net.cabezudo.sofia.core.sic.elements.SICUnexpectedEndOfCodeException;
import net.cabezudo.sofia.core.sic.objects.SICObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class SofiaImageCode {

  private Tokens tokens;
  private SICElement sicElement;
  private final String code;
  private Messages messages = new Messages();
  private Path basePath;

  public SofiaImageCode(Path basePath, String plainCode) {
    this(basePath, plainCode, false);
  }

  public SofiaImageCode(Path basePath, String plainCode, boolean formatCode) {
    this.basePath = basePath;
    if (plainCode == null) {
      throw new RuntimeException("null string parameter.");
    }
    if (plainCode.isBlank()) {
      throw new RuntimeException("Empty code.");
    }
    if (formatCode) {
      code = formatCode(plainCode);
    } else {
      code = plainCode;
    }
    tokens = Tokenizer.tokenize(code);

    SICFactory sicFactory = new SICFactory();
    try {
      sicElement = sicFactory.get(tokens);
    } catch (SICCompileTimeException e) {
      messages.add(new Message(e.getMessage(), e.getPosition()));
    } catch (SICUnexpectedEndOfCodeException e) {
      messages.add(new Message(e.getMessage()));
    }
  }

  public Tokens getTokens() {
    return tokens;
  }

  public SICObject compile() throws SICCompileTimeException {
    SICObject sicObject = sicElement.compile(basePath);
    return sicObject;
  }

  public String getShortCode() {
    return code;
  }

  public String getFormatedCode() {
    return sicElement.toString(0);
  }

  private String formatCode(String plainCode) {
    StringBuilder cleanCode = cleanCode(plainCode);
    StringBuilder sb = new StringBuilder(cleanCode.length() * 2);
    int tabs = 0;
    for (int i = 0; i < cleanCode.length(); i++) {
      char c = cleanCode.charAt(i);
      if (c == '(') {
        sb.append('(').append('\n');
        tabs++;
        sb.append(getSpaces(tabs));
        continue;
      }
      if (c == ',') {
        sb.append(',').append('\n');
        sb.append(getSpaces(tabs));
        continue;
      }
      if (c == ')') {
        tabs--;
        sb.append('\n').append(getSpaces(tabs)).append(')');
        continue;
      }
      sb.append(c);
    }
    return sb.toString();
  }

  private String getSpaces(int tabs) {
    return Utils.repeat(' ', tabs * 2);
  }

  private StringBuilder cleanCode(String code) {
    StringBuilder sb = new StringBuilder();
    char[] chars = code.toCharArray();
    for (char c : chars) {
      if (c != '\n' && c != ' ' && c != '\t' && c != '\u00A0') {
        sb.append(c);
      }
    }
    return sb;
  }

  public JSONArray getJSONMessages() {
    return messages.toJSON();
  }
}
