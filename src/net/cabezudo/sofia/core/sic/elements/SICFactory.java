package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.Tokens;
import net.cabezudo.sofia.core.sic.exceptions.EmptyQueueException;
import net.cabezudo.sofia.core.sic.exceptions.InvalidParameterNameException;
import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;
import net.cabezudo.sofia.core.sic.tokens.parameters.ParameterFactory;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class SICFactory {

  public SICElement get(Tokens tokens) throws SICCompileTimeException, SICUnexpectedEndOfCodeException {
    Token token;
    try {
      consumeSpaces(tokens);
      token = tokens.consume();
    } catch (EmptyQueueException e) {
      throw new SICUnexpectedEndOfCodeException(e);
    }
    if (!token.isFunction()) {
      token.setError(true);
      throw new SICCompileTimeException(token.getValue() + " is not a function.", token);
    }
    SICElement mainFunction;
    try {
      mainFunction = get(token, tokens);
    } catch (EmptyQueueException e) {
      throw new SICUnexpectedEndOfCodeException(e);
    }
    try {
      consumeSpaces(tokens);
      Token remainToken = tokens.peek();
      if (remainToken != null) {
        remainToken.setInvalidClass(true);
        remainToken.setError(true);
        throw new SICCompileTimeException("Unexpected token '" + remainToken.getValue() + "'. Extra token in code.", remainToken);
      }
    } catch (EmptyQueueException e) {
      throw new SICUnexpectedEndOfCodeException(e);
    }
    return mainFunction;
  }

  private SICElement get(Token token, Tokens tokens) throws SICCompileTimeException, EmptyQueueException {

    Position position;

    if (token.isFunction()) {
      return createSICFunction(token, tokens);
    }
    position = token.getPosition();
    token.setError(true);
    throw new SICCompileTimeException("Unexpected value " + token.getValue() + ".", token);
  }

  private void consumeSpaces(Tokens tokens) throws EmptyQueueException {
    Token token = tokens.peek();
    while (token != null && (token.isSpace() || token.isNewLine() || token.isTabulation())) {
      tokens.consume();
      token = tokens.peek();
    }
  }

  private SICElement createSICFunction(Token token, Tokens tokens) throws SICCompileTimeException, EmptyQueueException {
    SICFunction sicFunction = new SICFunction(token);
    Token openParentheses = tokens.consume();
    if (!openParentheses.isOpenParentheses()) {
      openParentheses.setError(true);
      throw new SICCompileTimeException("Unexpected token '" + openParentheses.getDescription() + "'. Must be a open parentheses.", openParentheses);
    }

    Token separatorOrCloseParentheses;
    do {
      consumeSpaces(tokens);
      Token parameterNameOrFunction = tokens.consume();

      if (parameterNameOrFunction.isParameterName()) {
        consumeSpaces(tokens);
        Token equal = tokens.consume();
        if (!equal.isEqual()) {
          equal.setError(true);
          throw new SICCompileTimeException("Unexpected token '" + equal.getDescription() + "'. Must be a equal.", equal);
        }
        consumeSpaces(tokens);
        Token parameterValue = tokens.consume();
        consumeSpaces(tokens);
        separatorOrCloseParentheses = tokens.consume();
        if (!separatorOrCloseParentheses.isComma() && !separatorOrCloseParentheses.isCloseParentheses()) {

          separatorOrCloseParentheses.setError(true);
          throw new SICCompileTimeException("Unexpected '" + separatorOrCloseParentheses.getValue() + "'. Must be a comma or close parentheses after a parameter.", separatorOrCloseParentheses);
        }
        SICParameter sicParameter;
        try {
          sicParameter = ParameterFactory.get(parameterNameOrFunction, parameterValue);
          sicFunction.add(sicParameter);
        } catch (InvalidParameterNameException e) {
          parameterNameOrFunction.setError(true);
          throw new SICCompileTimeException(e.getMessage(), separatorOrCloseParentheses);
        }
        // Go to the next iteracion searching other parameter or token
        continue;
      }

      if (parameterNameOrFunction.isFunction()) {
        SICElement newSICFunction = createSICFunction(parameterNameOrFunction, tokens);
        sicFunction.add(newSICFunction);
        consumeSpaces(tokens);
        separatorOrCloseParentheses = tokens.consume();
        if (!separatorOrCloseParentheses.isComma() && !separatorOrCloseParentheses.isCloseParentheses()) {
          separatorOrCloseParentheses.setError(true);
          throw new SICCompileTimeException("Invalid token " + separatorOrCloseParentheses.getDescription() + ". Must be a comma or close parentheses after a function.", separatorOrCloseParentheses);
        }
        // Go to the next iteracion searching other parameter or token
        continue;
      }
      parameterNameOrFunction.setInvalidClass(true);
      parameterNameOrFunction.setError(true);
      throw new SICCompileTimeException("Invalid token " + parameterNameOrFunction.getValue() + ". Must be a parameter or function.", parameterNameOrFunction);
    } while (separatorOrCloseParentheses != null && separatorOrCloseParentheses.isComma());

    return sicFunction;
  }
}
