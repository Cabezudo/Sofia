package net.cabezudo.sofia.core.sic;

import net.cabezudo.sofia.core.sic.elements.SICElement;
import net.cabezudo.sofia.core.sic.elements.SICFactory;
import net.cabezudo.sofia.core.sic.exceptions.SICException;
import net.cabezudo.sofia.core.sic.exceptions.SICParseException;
import net.cabezudo.sofia.core.sic.exceptions.UnexpectedElementException;
import net.cabezudo.sofia.core.sic.exceptions.UnexpectedTokenException;
import net.cabezudo.sofia.core.sic.objects.SICObject;
import net.cabezudo.sofia.core.sic.tokens.Position;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.12
 */
public class SofiaImageCode {

  private String plainCode;
  private Tokens tokens;
  private final SICElement sicElement;
  private final String code;

  public SofiaImageCode(String rawPlainCode) throws SICParseException, UnexpectedElementException, UnexpectedTokenException {
    if (rawPlainCode == null) {
      throw new SICParseException("null string parameter.", Position.INITIAL);
    }
    plainCode = rawPlainCode.trim();
    if (plainCode.isBlank()) {
      throw new SICParseException("Empty string.", Position.INITIAL);
    }
    tokens = Tokenizer.tokenize(plainCode);
    code = tokens.toCode();
    SICFactory sicFactory = new SICFactory();
    sicElement = sicFactory.get(tokens);
  }

  public SICObject compile() throws SICException {
    SICObject sicObject = sicElement.compile();
    return sicObject;
  }

  public String getCode() {
    return code;
  }

  public String getFormatedCode() {
    return sicElement.toString(0);
  }
}
