package net.cabezudo.sofia.core.sic.elements;

import net.cabezudo.sofia.core.sic.SICCompilerMessages;
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

  public SICElement get(Tokens tokens, SICCompilerMessages messages) {
    Token token;
    Position position = Position.INITIAL;
    try {
      consumeSpaces(tokens);
      token = tokens.consume();
    } catch (EmptyQueueException e) {
      messages.add("Unexpected end of code.", e.getPosition());
      return null;
    }
    if (!token.isFunction()) {
      messages.add(token.getValue() + " is not a function.", position);
      token.setError(true);
    }
    SICElement mainFunction = get(token, tokens, messages);
    try {
      consumeSpaces(tokens);
      Token remainToken = tokens.peek();
      if (remainToken != null) {
        messages.add("Unexpected token '" + remainToken.getValue() + "'.", remainToken.getPosition());
        remainToken.setInvalidClass(true);
        remainToken.setError(true);
      }
    } catch (EmptyQueueException e) {
      messages.add("Unexpected end of code.", e.getPosition());
      return null;
    }
    return mainFunction;
  }

  private SICElement get(Token token, Tokens tokens, SICCompilerMessages messages) {

    Position position;

    if (token.isFunction()) {
      try {
        return createSICFunction(token, tokens, messages);
      } catch (EmptyQueueException e) {
        messages.add("Unexpected end of function.", e.getPosition());
        return new SICUnfinishedFunction(token.getValue(), token.getPosition());
      }
    }
    position = token.getPosition();
    messages.add("Unexpected value " + token.getValue() + ".", position);
    token.setError(true);
    return new SICInvalidElement(token);
  }

  private void consumeSpaces(Tokens tokens) throws EmptyQueueException {
    Token token = tokens.peek();
    while (token != null && (token.isSpace() || token.isNewLine() || token.isTabulation())) {
      tokens.consume();
      token = tokens.peek();
    }
  }

  private SICElement createSICFunction(Token token, Tokens tokens, SICCompilerMessages messages) throws EmptyQueueException {
    SICFunction sicFunction = new SICFunction(token);
    Token openParentheses = tokens.consume();
    if (!openParentheses.isOpenParentheses()) {
      messages.add("Unexpected token '" + openParentheses.getDescription() + "'. Must be a open parentheses.", openParentheses.getPosition());
      openParentheses.setError(true);
      return new SICInvalidElement(token);
    }

    Token separatorOrCloseParentheses;
    do {
      consumeSpaces(tokens);
      Token parameterNameOrFunction = tokens.consume();
      if (parameterNameOrFunction.isParameterName()) {
        consumeSpaces(tokens);
        Token equal = tokens.consume();
        if (!equal.isEqual()) {
          messages.add("Unexpected token '" + equal.getDescription() + "'. Must be a equal.", equal.getPosition());
          equal.setError(true);
          return new SICInvalidElement(parameterNameOrFunction);
        }
        consumeSpaces(tokens);
        Token parameterValue = tokens.consume();
        consumeSpaces(tokens);
        separatorOrCloseParentheses = tokens.consume();
        if (!separatorOrCloseParentheses.isComma() && !separatorOrCloseParentheses.isCloseParentheses()) {
          messages.add("Unexpected token '" + separatorOrCloseParentheses.getDescription() + "'. Must be a comma or close parentheses.", separatorOrCloseParentheses.getPosition());
          separatorOrCloseParentheses.setError(true);
          return new SICInvalidElement(parameterNameOrFunction);
        }
        SICParameter sicParameter;
        try {
          sicParameter = ParameterFactory.get(parameterNameOrFunction, parameterValue);
          sicFunction.add(sicParameter);
        } catch (InvalidParameterNameException e) {
          messages.add(e.getMessage(), e.getPosition());
          parameterNameOrFunction.setError(true);
          return new SICInvalidElement(parameterNameOrFunction);
        }
        continue;
      }
      if (parameterNameOrFunction.isFunction()) {
        SICElement newSICFunction = createSICFunction(parameterNameOrFunction, tokens, messages);
        sicFunction.add(newSICFunction);
        separatorOrCloseParentheses = tokens.consume();
        if (!separatorOrCloseParentheses.isComma() && !separatorOrCloseParentheses.isCloseParentheses()) {
          messages.add("Invalid token " + separatorOrCloseParentheses + ". Must be a comma or close parentheses.", separatorOrCloseParentheses.getPosition());
          separatorOrCloseParentheses.setError(true);
          return new SICInvalidElement(parameterNameOrFunction);
        }
        continue;
      }
      messages.add("Invalid token " + parameterNameOrFunction.getValue() + ". Must be a parameter or function.", parameterNameOrFunction.getPosition());
      parameterNameOrFunction.setInvalidClass(true);
      parameterNameOrFunction.setError(true);
      return new SICInvalidElement(parameterNameOrFunction);
    } while (separatorOrCloseParentheses != null && separatorOrCloseParentheses.isComma());

    return sicFunction;
  }
}
