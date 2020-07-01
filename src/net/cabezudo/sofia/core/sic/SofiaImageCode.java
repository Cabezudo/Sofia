package net.cabezudo.sofia.core.sic;

import net.cabezudo.sofia.core.Utils;
import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFactory;
import net.cabezudo.sofia.core.sic.objects.SICObject;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class SofiaImageCode {

  private String plainCode;
  private Tokens tokens;
  private final SICElement sicElement;
  private final String code;
  private final SICCompilerMessages messages;

  public SofiaImageCode(String plainCode) {
    this(plainCode, false);
  }

  public SofiaImageCode(String plainCode, boolean formatCode) {
    this.messages = new SICCompilerMessages();
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
    tokens = Tokenizer.tokenize(code, messages);

    SICFactory sicFactory = new SICFactory();
    sicElement = sicFactory.get(tokens, messages);
  }

  public Tokens getTokens() {
    return tokens;
  }

  public SICObject compile() {
    SICObject sicObject = sicElement.compile(messages);
    return sicObject;
  }

  public String getShortCode() {
    return code;
  }

  public String getFormatedCode() {
    return sicElement.toString(0);
  }

  public SICCompilerMessages getCompilerMessages() {
    return messages;
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
}
