package net.cabezudo.sofia.core.sic;

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
    this.messages = new SICCompilerMessages();
    if (plainCode == null) {
      throw new RuntimeException("null string parameter.");
    }
    if (plainCode.isBlank()) {
      throw new RuntimeException("Empty code.");
    }
    tokens = Tokenizer.tokenize(plainCode, messages);
    code = tokens.toCode();
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
}
