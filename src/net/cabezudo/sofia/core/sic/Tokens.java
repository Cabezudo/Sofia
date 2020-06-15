package net.cabezudo.sofia.core.sic;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import net.cabezudo.sofia.core.sic.exceptions.EmptyQueueException;
import net.cabezudo.sofia.core.sic.exceptions.UnexpectedElementException;
import net.cabezudo.sofia.core.sic.tokens.Position;
import net.cabezudo.sofia.core.sic.tokens.Token;

/**
 * @author <a href="http://cabezudo.net">Esteban Cabezudo</a>
 * @version 0.01.00, 2020.06.13
 */
public class Tokens implements Iterable<Token> {

  private Position position;
  private final Queue<Token> queue = new LinkedList<>();

  public String toCode() {
    StringBuilder sb = new StringBuilder();
    for (Token token : queue) {
      sb.append(token.getValue());
    }
    return sb.toString();
  }

  public boolean add(Token token) throws UnexpectedElementException {
    if (token == null || token.isEmpty()) {
      return false;
    }
    if (token.isSpace()) {
      return false;
    }

    if (queue.isEmpty()) {
      position = token.getPosition();
    }

    return queue.offer(token);
  }

  public Token element() throws EmptyQueueException {
    Token token = queue.element();
    if (token == null) {
      throw new EmptyQueueException();
    }
    return token;
  }

  public Position getPosition() {
    return position;
  }

  boolean hasNext() {
    return queue.size() > 0;
  }

  public Token consume() throws EmptyQueueException {
    Token token = queue.poll();
    if (token == null) {
      throw new EmptyQueueException();
    }
    return token;
  }

  @Override
  public Iterator<Token> iterator() {
    return queue.iterator();
  }
}
