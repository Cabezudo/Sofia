package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.Tokens;
import net.cabezudo.sofia.core.sic.exceptions.EOSException;
import net.cabezudo.sofia.core.sic.exceptions.EmptyQueueException;
import net.cabezudo.sofia.core.sic.exceptions.InvalidParameterNameException;
import net.cabezudo.sofia.core.sic.exceptions.SICParseException;
import net.cabezudo.sofia.core.sic.exceptions.UnexpectedElementException;
import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;
import net.cabezudo.sofia.core.sic.tokens.parameters.ParameterFactory;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICFactory {

  public SICElement get(Tokens tokens) throws SICParseException, EmptyQueueException {
    Token token;
    Position position = Position.INITIAL;
    try {
      token = tokens.consume();
      if (!token.isFunction()) {
        throw new SICParseException(token.getValue() + "() is not a function.", position);
      }
    } catch (EmptyQueueException e) {
      throw new EOSException(position);
    }
    return get(token, tokens);
  }

  private SICElement get(Token token, Tokens tokens) throws SICParseException, EmptyQueueException {

    Position position;

    if (token.isFunction()) {
      return createSICFunction(token, tokens);
    }
    position = token.getPosition();
    throw new UnexpectedElementException("value", token.getValue(), position);
  }

  private SICFunction createSICFunction(Token token, Tokens tokens) throws EmptyQueueException, UnexpectedElementException, InvalidParameterNameException {
    SICFunction sicFunction = new SICFunction(token);
    Token openParentheses = tokens.consume();
    if (!openParentheses.isOpenParentheses()) {
      throw new UnexpectedElementException("open parentheses", openParentheses.getValue(), openParentheses.getPosition());
    }

    Token separatorOrCloseParentheses;
    do {
      Token parameterNameOrFunction = tokens.consume();
      if (parameterNameOrFunction.isParameterName()) {
        Token equal = tokens.consume();
        if (!equal.isEqual()) {
          throw new UnexpectedElementException("open parentheses", openParentheses.getValue(), openParentheses.getPosition());
        }
        Token parameterValue = tokens.consume();
        separatorOrCloseParentheses = tokens.consume();
        if (!separatorOrCloseParentheses.isComma() && !separatorOrCloseParentheses.isCloseParentheses()) {
          throw new UnexpectedElementException(
                  "comma or close parentheses", openParentheses.getValue(), openParentheses.getPosition());
        }
        SICParameter sicParameter = ParameterFactory.get(parameterNameOrFunction, parameterValue);
        sicFunction.add(sicParameter);
        continue;
      }
      if (parameterNameOrFunction.isFunction()) {
        SICFunction newSICFunction = createSICFunction(parameterNameOrFunction, tokens);
        sicFunction.add(newSICFunction);
        separatorOrCloseParentheses = tokens.consume();
        if (!separatorOrCloseParentheses.isComma() && !separatorOrCloseParentheses.isCloseParentheses()) {
          throw new UnexpectedElementException(
                  "comma or close parentheses", openParentheses.getValue(), openParentheses.getPosition());
        }
        continue;
      }
      throw new UnexpectedElementException("parameter or function", parameterNameOrFunction.getValue(), parameterNameOrFunction.getPosition());
    } while (separatorOrCloseParentheses != null && separatorOrCloseParentheses.isComma());

    return sicFunction;
  }
}
